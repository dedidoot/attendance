package gapara.co.id.core.model

data class StatusModel(var status: String? = null) {

    fun getRealStatus(): String {
        return if (status?.equals(PENDING, ignoreCase = true) == true) {
            "Open"
        } else if (status?.equals(REQUEST_COMPLETE, ignoreCase = true) == true) {
            "On Progress"
        } else {
            "Done"
        }
    }

    fun isOpen(): Boolean {
        return status?.equals(PENDING, ignoreCase = true) == true
    }

    fun isRequestComplete(): Boolean {
        return status?.equals(REQUEST_COMPLETE, ignoreCase = true) == true
    }

    companion object {
        const val PENDING = "pending"
        const val REQUEST_COMPLETE = "request_complete"
        const val DONE = "complete"
    }
}