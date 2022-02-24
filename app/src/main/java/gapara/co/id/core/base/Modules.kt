package gapara.co.id.core.base

import gapara.co.id.feature.announcement.AnnouncementViewModel
import gapara.co.id.feature.brief.BriefViewModel
import gapara.co.id.feature.checkpoint.CheckPointViewModel
import gapara.co.id.feature.emergency.EmergencyReportViewModel
import gapara.co.id.feature.feedback.FeedbackViewModel
import gapara.co.id.feature.home.HomeViewModel
import gapara.co.id.feature.incident.AddNewsViewModel
import gapara.co.id.feature.incident.IncidentViewModel
import gapara.co.id.feature.initial.InitialViewModel
import gapara.co.id.feature.intel.IntelViewModel
import gapara.co.id.feature.login.LoginViewModel
import gapara.co.id.feature.lost_found.LostFoundViewModel
import gapara.co.id.feature.report.ReportViewModel
import gapara.co.id.feature.special_report.SpecialReportViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

object Modules {

    fun getModules(): List<Module> {
        val module = module {
            viewModel { LoginViewModel() }
            viewModel { HomeViewModel() }
            viewModel { BriefViewModel() }
            viewModel { AnnouncementViewModel() }
            viewModel { IncidentViewModel() }
            viewModel { EmergencyReportViewModel() }
            viewModel { FeedbackViewModel() }
            viewModel { ReportViewModel() }
            viewModel { InitialViewModel() }
            viewModel { CheckPointViewModel() }
            viewModel { SpecialReportViewModel() }
            viewModel { LostFoundViewModel() }
            viewModel { IntelViewModel() }
            viewModel { AddNewsViewModel() }
        }
        return listOf(module)
    }
}