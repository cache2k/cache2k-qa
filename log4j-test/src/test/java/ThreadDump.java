import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Thread dump generation, inspired from: http://crunchify.com/how-to-generate-java-thread-dump-programmatically/
 *
 * @author Jens Wilke; created: 2014-09-13
 */
public class ThreadDump {

  public static String generateThreadDump() {
    final StringBuilder sb = new StringBuilder();
    final ThreadMXBean _threadMXBean = ManagementFactory.getThreadMXBean();
    final ThreadInfo[] _infos =
      _threadMXBean.getThreadInfo(_threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
    for (ThreadInfo _info : _infos) {
      sb.append("Thread \"");
      sb.append(_info.getThreadName());
      sb.append("\" ");
      final Thread.State _state = _info.getThreadState();
      sb.append(_state);
      final StackTraceElement[] stackTraceElements = _info.getStackTrace();
      for (final StackTraceElement stackTraceElement : stackTraceElements) {
        sb.append("\n    at ");
        sb.append(stackTraceElement);
      }
      sb.append("\n\n");
    }
    return sb.toString();
  }

}
