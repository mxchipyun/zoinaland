package wendu.dsbridge.ext

import kotlinx.coroutines.*

fun runDelay(delayTime: Long? = 1, executeFunc: () -> Unit) {
    GlobalScope.launch {
        delay((delayTime ?: 1L) * 1000)
        withContext(Dispatchers.Main) {
            executeFunc.invoke()
        }
    }
}
fun runDelayMillions(delayTime: Long, executeFunc: () -> Unit) {
    GlobalScope.launch {
        delay(delayTime)
        withContext(Dispatchers.Main) {
            executeFunc.invoke()
        }
    }
}

fun runUI(executeFunc: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        executeFunc.invoke()
    }
}

fun runThread(executeFunc: () -> Unit) {
    GlobalScope.launch(Dispatchers.Default) {
        executeFunc.invoke()
    }
}