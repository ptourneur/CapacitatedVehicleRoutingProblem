package com.polytech.ui;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;

public class AppView implements FxmlView<AppViewModel> {

    @InjectViewModel
    AppViewModel viewModel;
}
