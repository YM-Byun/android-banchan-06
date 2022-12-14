package com.woowahan.banchan.ui.soupdishbanchan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentSoupDishBanchanBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.DefaultBanchanAdapter
import com.woowahan.banchan.ui.adapter.decoratin.GridItemDecoration
import com.woowahan.banchan.ui.base.BaseFragment
import com.woowahan.banchan.ui.cart.CartActivity
import com.woowahan.banchan.ui.detail.BanchanDetailActivity
import com.woowahan.banchan.ui.viewmodel.SoupDishBanchanViewModel
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SoupDishBanchanFragment : BaseFragment<FragmentSoupDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_soup_dish_banchan

    private val viewModel: SoupDishBanchanViewModel by viewModels()
    private val adapter: DefaultBanchanAdapter by lazy {
        DefaultBanchanAdapter(
            getString(R.string.soup_dish_banchan_banner_title),
            viewModel.filter,
            BanchanModel.getFilterList(),
            viewModel.filterItemSelect,
            viewModel.clickInsertCartButton,
            viewModel.itemClickListener
        )
    }

    private val spanCount = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.layoutErrorView.viewModel = viewModel

        setUpGridRecyclerView()
        observeData()
    }

    private fun setUpGridRecyclerView() {
        binding.rvSoupDish.layoutManager = GridLayoutManager(context, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position < 2) {
                        true -> spanCount
                        else -> 1
                    }
                }
            }
        }

        binding.rvSoupDish.addItemDecoration(gridItemDecoration)
    }

    private fun observeData() {
        repeatOnStarted {
            launch {
                viewModel.eventFlow.collect {
                    when (it) {
                        is SoupDishBanchanViewModel.UiEvent.ShowToast -> showToast(
                            context,
                            it.message
                        )

                        is SoupDishBanchanViewModel.UiEvent.ShowSnackBar -> showSnackBar(
                            it.message,
                            binding.layoutBackground
                        )

                        is SoupDishBanchanViewModel.UiEvent.ShowDialog -> {
                            DialogUtil.show(childFragmentManager, it.dialogBuilder)
                        }

                        is SoupDishBanchanViewModel.UiEvent.ShowCartBottomSheet -> {
                            it.bottomSheet.show(childFragmentManager)
                        }

                        is SoupDishBanchanViewModel.UiEvent.ShowCartView -> {
                            startActivity(CartActivity.get(requireContext()))
                        }

                        is SoupDishBanchanViewModel.UiEvent.ShowDetailView -> {
                            startActivity(
                                BanchanDetailActivity.get(
                                    requireContext(),
                                    it.banchanModel.hash,
                                    it.banchanModel.title
                                )
                            )
                        }
                    }
                }
            }
            launch {
                viewModel.banchans.collectLatest {
                    adapter.updateList(it)
                }
            }
        }
    }

    private val gridItemDecoration by lazy {
        GridItemDecoration(
            requireContext(),
            spanCount
        ).decoration
    }
}