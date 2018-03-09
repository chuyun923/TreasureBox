public class GcHook {

    /**
     * 解决析构函数执行时，进程被挂起；
     * 析构看门狗超时TimeoutException
     * http://blog.csdn.net/jamin0107/article/details/78793021
     */
    public static void hook() {
        if (Build.VERSION.SDK_INT > 25) {
            return;
        }
        
        try {
            Class watchdogCls = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            Field instanceField = watchdogCls.getDeclaredField("INSTANCE");
            instanceField.setAccessible(true);
            Object instance = instanceField.get(null);
            
            Class daemonCls = Class.forName("java.lang.Daemons$Daemon");
            Method stopMethod = daemonCls.getDeclaredMethod("stop");
            stopMethod.setAccessible(true);
            Field threadField = daemonCls.getDeclaredField("thread");
            threadField.setAccessible(true);
            Object thread = threadField.get(instance);
            
            Method isRunningMethod = daemonCls.getDeclaredMethod("isRunning");
            isRunningMethod.setAccessible(true);
            if ((boolean) isRunningMethod.invoke(instance)) {
                stopMethod.invoke(instance);
                threadField.set(instance, thread);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
