package com.woowahan.banchan.ui.maindishbanchan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentMainDishBanchanBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.ViewModeToggleBanchanAdapter
import com.woowahan.banchan.ui.adapter.decoratin.GridItemDecoration
import com.woowahan.banchan.ui.base.BaseFragment
import com.woowahan.banchan.ui.cart.CartActivity
import com.woowahan.banchan.ui.detail.BanchanDetailActivity
import com.woowahan.banchan.ui.viewmodel.MainDishBanchanViewModel
import com.woowahan.banchan.util.DialogUtil
import com.woowahan.domain.model.BanchanModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainDishBanchanFragment : BaseFragment<FragmentMainDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_main_dish_banchan

    private val viewModel: MainDishBanchanViewModel by viewModels()
    private val adapter: ViewModeToggleBanchanAdapter by lazy {
        ViewModeToggleBanchanAdapter(
            getString(R.string.main_dish_banchan_banner_title),
            viewModel.filter,
            viewModel.gridViewModel,
            BanchanModel.getFilterList(),
            viewModel.filterItemSelect,
            viewModel.viewModeToggleEvent,
            viewModel.clickInsertCartButton,
            viewModel.itemClickListener
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.layoutErrorView.viewModel = viewModel

        setUpRecyclerView()
        observeData()
    }

    private fun setUpRecyclerView(){
        binding.rvMainDish.layoutManager = GridLayoutManager(context, viewModel.spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position < 2) {
                        true -> spanCount
                        else -> 1
                    }
                }
            }
        }
        when(viewModel.gridViewModel){
            true -> setUpGridRecyclerView()
            false -> setUpLinearRecyclerView()
        }
    }

    private fun setUpGridRecyclerView() {
        (binding.rvMainDish.layoutManager as GridLayoutManager).spanCount = viewModel.spanCount
        binding.rvMainDish.let {
            while(it.itemDecorationCount != 0) it.removeItemDecorationAt(it.itemDecorationCount - 1)
            it.addItemDecoration(gridItemDecoration)
        }
    }

    private fun setUpLinearRecyclerView() {
        binding.rvMainDish.let {
            while (it.itemDecorationCount != 0) it.removeItemDecorationAt(it.itemDecorationCount - 1)
        }
        (binding.rvMainDish.layoutManager as GridLayoutManager).spanCount = viewModel.spanCount
    }

    private fun observeData() {
        repeatOnStarted {
            launch {
                viewModel.eventFlow.collect {
                    when (it) {
                        is MainDishBanchanViewModel.UiEvent.ShowToast -> showToast(context, it.message)

                        is MainDishBanchanViewModel.UiEvent.ShowSnackBar -> showSnackBar(
                            it.message,
                            binding.layoutBackground
                        )

                        is MainDishBanchanViewModel.UiEvent.ShowDialog -> {
                            DialogUtil.show(childFragmentManager, it.dialogBuilder)
                        }

                        is MainDishBanchanViewModel.UiEvent.ShowCartBottomSheet -> {
                            it.bottomSheet.show(childFragmentManager)
                        }

                        is MainDishBanchanViewModel.UiEvent.ShowCartView -> {
                            startActivity(CartActivity.get(requireContext()))
                        }

                        is MainDishBanchanViewModel.UiEvent.ShowDetailView -> {
                            startActivity(BanchanDetailActivity.get(requireContext(), it.banchanModel.hash, it.banchanModel.title))
                        }

                        is MainDishBanchanViewModel.UiEvent.ChangeViewMode -> {
                            Timber.d("gridViewMode => ${it.isGridMode}")
                            when(it.isGridMode){
                                true -> setUpGridRecyclerView()
                                else -> setUpLinearRecyclerView()
                            }
                            adapter.refreshList()
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
            viewModel.gridSpanCount
        ).decoration
    }
}