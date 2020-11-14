package com.afzaln.awakedebug.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.afzaln.awakedebug.BuildConfig
import com.afzaln.awakedebug.R
import com.afzaln.awakedebug.databinding.AboutFragmentBinding
import com.afzaln.awakedebug.databinding.AboutListHeaderBinding
import com.afzaln.awakedebug.databinding.AboutListItemBinding


class AboutFragment : Fragment() {

    companion object {
        fun newInstance() = AboutFragment()
    }

    private var _binding: AboutFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AboutFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.aboutInfoList.adapter = AboutItemAdapter {
            val item = AboutItemAdapter.items[it] as? Item.AboutItem ?: return@AboutItemAdapter
            when (item.title) {
                R.string.licenses -> {
                    (activity as? MainActivity)?.navigateToLicenses()
                }
                R.string.fork_on_github -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_repo_url))))
                }
                R.string.visit_my_website -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_url))))
                }
            }
        }
    }
}

class AboutItemAdapter(private val onClick: (Int) -> Unit) : RecyclerView.Adapter<ItemHolder>() {
    companion object {
        val items = listOf(
            Item.AboutItem(R.mipmap.ic_launcher_round, R.string.app_name, R.string.author_username, 0),
            Item.AboutItem(R.drawable.ic_info, R.string.version, subtitleString = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"),
            Item.AboutItem(R.drawable.ic_licenses, R.string.licenses, 0, clickable = true),
            Item.AboutItem(R.drawable.ic_info, R.string.verifiable_builds_note, 0),
            Item.HeaderItem(R.string.author_header),
            Item.AboutItem(R.drawable.ic_github_logo, R.string.fork_on_github, R.string.github_repo_url, clickable = true),
            Item.AboutItem(R.drawable.ic_link, R.string.visit_my_website, R.string.website_url, clickable = true)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.about_list_item -> AboutItemHolder(AboutListItemBinding.inflate(inflater, parent, false), onClick)
            R.layout.about_list_header -> HeaderItemHolder(AboutListHeaderBinding.inflate(inflater, parent, false))
            else                     -> error("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        when (holder) {
            is AboutItemHolder -> holder.bind(items[position] as Item.AboutItem)
            is HeaderItemHolder -> holder.bind(items[position] as Item.HeaderItem)
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].itemType

    override fun getItemCount(): Int = items.size
}

sealed class Item(
    @LayoutRes open val itemType: Int
) {
    data class AboutItem(
        @DrawableRes val icon: Int,
        @StringRes val title: Int = 0,
        @StringRes val subtitle: Int = 0,
        @ColorRes val tintColor: Int = R.color.aboutMenuIconTint,
        val subtitleString: String = "",
        val clickable: Boolean = false
    ) : Item(R.layout.about_list_item)

    data class HeaderItem(
        val title: Int,
    ) : Item(R.layout.about_list_header)
}

sealed class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class HeaderItemHolder(
    private val binding: AboutListHeaderBinding
) : ItemHolder(binding.root) {
    fun bind(item: Item.HeaderItem) {
        binding.title.text = itemView.resources.getString(item.title)
    }
}

class AboutItemHolder(
    private val binding: AboutListItemBinding,
    private val onClick: (Int) -> Unit
) : ItemHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            onClick(adapterPosition)
        }
    }

    fun bind(item: Item.AboutItem) {
        itemView.isClickable = item.clickable
        binding.appIcon.setImageResource(item.icon)

        if (item.tintColor == 0) {
            binding.appIcon.imageTintList = null
        } else {
            binding.appIcon.imageTintList = ColorStateList.valueOf(itemView.resources.getColor(item.tintColor, itemView.context.theme))
        }

        binding.title.text = itemView.resources.getString(item.title)
        when {
            item.subtitle != 0               -> {
                binding.subtitle.visibility = View.VISIBLE
                binding.subtitle.text = itemView.resources.getString(item.subtitle)
            }
            item.subtitleString.isNotEmpty() -> {
                binding.subtitle.visibility = View.VISIBLE
                binding.subtitle.text = item.subtitleString
            }
            else                             -> {
                binding.subtitle.visibility = View.GONE
            }
        }
    }
}
