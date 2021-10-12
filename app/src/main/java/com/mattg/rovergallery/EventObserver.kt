package com.mattg.rovergallery

/**
 * Class to observe events, triggers event logic and returns the data.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>?> {

    override fun onUpdate(data: Event<T>?): Event<T>? {
        data?.getContentIfNotHandled()?.let { value -> onEventUnhandledContent(value) }
        return super.onUpdate(data)
    }
}