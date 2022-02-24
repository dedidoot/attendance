package gapara.co.id.core.base.view

import android.view.GestureDetector
import android.view.MotionEvent
import gapara.co.id.core.model.OnSwipeTouchListener

class GestureListener(private val onSwipeTouchListener: OnSwipeTouchListener) : GestureDetector.SimpleOnGestureListener() {

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        var isScroll = false
        val deltaY = (e2?.y ?: 0f) - (e1?.y ?: 0f)
        val deltaX = (e2?.x ?: 0f) - (e1?.x ?: 0f)
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (Math.abs(deltaX) > SLIDE_THRESHOLD) {
                if (deltaX > 0) {
                    onSwipeTouchListener.onSwipeRight()
                } else {
                    onSwipeTouchListener.onSwipeLeft()
                }
                isScroll = true
            }
        } else {
            if (Math.abs(deltaY) > SLIDE_THRESHOLD) {
                if (deltaY > 0) {
                    onSwipeTouchListener.onSwipeBottom()
                } else {
                    onSwipeTouchListener.onSwipeTop()
                }
                isScroll = true
            }
        }
        return isScroll
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        var isResult = false
        val diffY = e2.y - e1.y
        val diffX = e2.x - e1.x
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeTouchListener.onSwipeRight()
                } else {
                    onSwipeTouchListener.onSwipeLeft()
                }
                isResult = true
            }
        } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffY > 0) {
                onSwipeTouchListener.onSwipeBottom()
            } else {
                onSwipeTouchListener.onSwipeTop()
            }
            isResult = true
        }
        return isResult
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
        private const val SLIDE_THRESHOLD = 100
    }
}