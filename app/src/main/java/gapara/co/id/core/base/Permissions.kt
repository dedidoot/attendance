package gapara.co.id.core.base

import gapara.co.id.core.model.GeneralModel

class Permissions(var permissionsData: ArrayList<GeneralModel>) {

    var isCreateIntelReport = false
    var isViewIntelReport = false
    var isViewBrief  = false
    var isViewBriefReport  = false
    var isViewSchedule  = false
    var isViewAnnouncement  = false
    var isViewCheckPoint  = false
    var isCreateLostAndFoundReport  = false
    var isCreateLogBookReport  = false
    var isViewIncident  = false
    var isViewIncidentReport = false
    var isViewFindingReport  = false
    var isCheckIn  = false
    var isCheckOut = false
    var isDoPresence = false
    var isCreateFollowUpIncident = false
    var isCommentIncident = false
    var isCreateIncidentReport = false

    init {
        isCreateIntelReport = permissionsData.find { isCreateIntelReport(it.name) }?.name.isNullOrBlank().not()
        isViewIntelReport = permissionsData.find { isViewIntelReport(it.name) }?.name.isNullOrBlank().not()
        isViewBrief = permissionsData.find { isViewBrief(it.name) }?.name.isNullOrBlank().not()
        isViewBriefReport = permissionsData.find { isViewBriefReport(it.name) }?.name.isNullOrBlank().not()
        isViewSchedule = permissionsData.find { isViewSchedule(it.name) }?.name.isNullOrBlank().not()
        isViewAnnouncement = permissionsData.find { isViewAnnouncement(it.name) }?.name.isNullOrBlank().not()
        isViewCheckPoint = permissionsData.find { isViewCheckPoint(it.name) }?.name.isNullOrBlank().not()
        isCreateLostAndFoundReport = permissionsData.find { isCreateLostAndFoundReport(it.name) }?.name.isNullOrBlank().not()
        isCreateLogBookReport = permissionsData.find { isCreateLogBookReport(it.name) }?.name.isNullOrBlank().not()
        isViewIncident = permissionsData.find { isViewIncident(it.name) }?.name.isNullOrBlank().not()
        isViewIncidentReport = permissionsData.find { isViewIncidentReport(it.name) }?.name.isNullOrBlank().not()
        isViewFindingReport = permissionsData.find { isViewFindingReport(it.name) }?.name.isNullOrBlank().not()
        isCheckIn = permissionsData.find { isCheckIn(it.name) }?.name.isNullOrBlank().not()
        isCheckOut = permissionsData.find { isCheckOut(it.name) }?.name.isNullOrBlank().not()
        isDoPresence = permissionsData.find { isDoPresence(it.name) }?.name.isNullOrBlank().not()
        isCreateFollowUpIncident = permissionsData.find { isCreateFollowUpIncident(it.name) }?.name.isNullOrBlank().not()
        isCommentIncident = permissionsData.find { isCommentIncident(it.name) }?.name.isNullOrBlank().not()
        isCreateIncidentReport = permissionsData.find { isCreateIncidentReport(it.name) }?.name.isNullOrBlank().not()
    }

    fun isCreateIntelReport(text : String?) : Boolean {
        return text == "create-intel-report"
    }

    fun isViewIntelReport(text : String?) : Boolean {
        return text == "view-intel-report"
    }

    fun isCreateBrief(text : String?) : Boolean {
        return text == "create-brief"
    }

    fun isViewBrief(text : String?) : Boolean {
        return text == "view-brief"
    }

    fun isAcceptBrief(text : String?) : Boolean {
        return text == "accept-brief"
    }

    fun isCreateIncident(text : String?) : Boolean {
        return text == "create-incident"
    }

    fun isViewIncident(text : String?) : Boolean {
        return text == "view-incident"
    }

    fun isCommentIncident(text : String?) : Boolean {
        return text == "create-comment-incident"
    }

    fun isCreateFollowUpIncident(text : String?) : Boolean {
        return text == "create-follow-up-incident"
    }

    fun isViewFollowUpIncident(text : String?) : Boolean {
        return text == "view-follow-up-incident"
    }

    fun isCreateReportNews(text : String?) : Boolean {
        return text == "create-report-news"
    }

    fun isViewReportNews(text : String?) : Boolean {
        return text == "view-report-news"
    }

    fun isCreateIncidentReport(text : String?) : Boolean {
        return text == "create-incident-report"
    }

    fun isViewIncidentReport(text : String?) : Boolean {
        return text == "view-incident-report"
    }

    fun isCreateFindingReport(text : String?) : Boolean {
        return text == "create-finding-report"
    }

    fun isViewFindingReport(text : String?) : Boolean {
        return text == "view-finding-report"
    }

    fun isCreateLostAndFoundReport(text : String?) : Boolean {
        return text == "create-lost-and-found-report"
    }

    fun isViewLostAndFoundReport(text : String?) : Boolean {
        return text == "view-lost-and-found-report"
    }

    fun isCreateLogBookReport(text : String?) : Boolean {
        return text == "create-log-book-report"
    }

    fun isViewLogBookReport(text : String?) : Boolean {
        return text == "view-log-book-report"
    }

    fun isCreateBriefReport(text : String?) : Boolean {
        return text == "create-brief-report"
    }

    fun isViewBriefReport(text : String?) : Boolean {
        return text == "view-brief-report"
    }

    fun isCreatePresence(text : String?) : Boolean {
        return text == "create-presence"
    }

    fun isViewPresence(text : String?) : Boolean {
        return text == "view-presence"
    }

    fun isDoPresence(text : String?) : Boolean {
        return text == "do-presence"
    }

    fun isCreateAnnouncement(text : String?) : Boolean {
        return text == "create-announcement"
    }

    fun isAcceptAnnouncement(text : String?) : Boolean {
        return text == "accept-announcement"
    }

    fun isViewAnnouncement(text : String?) : Boolean {
        return text == "view-announcement"
    }

    fun isViewCheckPoint(text : String?) : Boolean {
        return text == "view-check-point"
    }

    fun isDoCheckPoint(text : String?) : Boolean {
        return text == "do-check-point"
    }

    fun isCreateCheckPointReport(text : String?) : Boolean {
        return text == "create-check-point-report"
    }

    fun isViewCheckPointReport(text : String?) : Boolean {
        return text == "view-check-point-report"
    }

    fun isCheckIn(text : String?) : Boolean {
        return text == "check-in"
    }

    fun isCheckOut(text : String?) : Boolean {
        return text == "check-out"
    }

    fun isViewSchedule(text : String?) : Boolean {
        return text == "view-schedule"
    }
}