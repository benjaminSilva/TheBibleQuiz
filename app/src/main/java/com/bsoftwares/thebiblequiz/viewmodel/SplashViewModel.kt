package com.bsoftwares.thebiblequiz.viewmodel

import com.bsoftwares.thebiblequiz.data.repositories.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    repo: BaseRepository
) : BaseViewModel(repo,false)