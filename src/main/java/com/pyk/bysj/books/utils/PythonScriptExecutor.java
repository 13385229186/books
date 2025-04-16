package com.pyk.bysj.books.utils;

import com.pyk.bysj.books.exception.script.ScriptExecutionException;
import lombok.Getter;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Python 脚本执行器
 */
public class PythonScriptExecutor {

  /**
   * 执行 Python 脚本
   * @param scriptPath 脚本路径（classpath 相对路径）
   * @param argsMap 参数键值对
   * @param timeout 超时时间（分钟）
   * @return 脚本输出结果
   * @throws ScriptExecutionException 脚本执行异常
   */
  public static String executePythonScript(String scriptPath,
                                           Map<String, String> argsMap,
                                           long timeout) throws ScriptExecutionException {
    try {
      // 1. 获取脚本绝对路径
      String scriptAbsolutePath = getScriptAbsolutePath(scriptPath);

      // 2. 构建命令参数
      List<String> command = buildCommand(scriptAbsolutePath, argsMap);

      // 3. 执行进程
      ProcessResult result = executeProcess(command, timeout);

      // 4. 检查执行结果
      if (result.getExitCode() != 0) {
        String errorMsg = !result.getErrorOutput().isEmpty() ?
                result.getErrorOutput() : result.getStandardOutput();
        throw new ScriptExecutionException("Python脚本执行失败: " + errorMsg, 500);
      }

      return result.getStandardOutput();
    } catch (IOException e) {
      throw new ScriptExecutionException("IO异常: " + e.getMessage(), 500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ScriptExecutionException("执行被中断: " + e.getMessage(), 500);
    } catch (TimeoutException e) {
      throw new ScriptExecutionException("执行超时", 500);
    } catch (Exception e) {
      throw new ScriptExecutionException("执行异常: " + e.getMessage(), 500);
    }
  }

  /**
   * 获取脚本绝对路径
   */
  private static String getScriptAbsolutePath(String scriptPath) throws UnsupportedEncodingException {
    URL resourceUrl = Objects.requireNonNull(
            PythonScriptExecutor.class.getResource("/scripts/" + scriptPath));
    String decodedPath = URLDecoder.decode(resourceUrl.getPath(), StandardCharsets.UTF_8);

    // Windows 路径处理
    if (decodedPath.startsWith("/") && System.getProperty("os.name").contains("Win")) {
      return decodedPath.substring(1);
    }
    return decodedPath;
  }

  /**
   * 构建执行命令
   */
  private static List<String> buildCommand(String scriptPath, Map<String, String> argsMap) {
    List<String> command = new ArrayList<>();
    command.add("python");
    command.add(scriptPath);

    // 添加命名参数
    argsMap.forEach((key, value) -> {
      command.add("--" + key);
      command.add(normalizePath(value));
    });

    return command;
  }

  /**
   * 路径规范化（统一使用正斜杠）
   */
  private static String normalizePath(String path) {
    return path != null ? path.replace("\\", "/") : "";
  }

  /**
   * 执行进程并获取结果
   */
  private static ProcessResult executeProcess(List<String> command,
                                              long timeoutMinutes) throws IOException, InterruptedException, TimeoutException {
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(false); // 分开获取stdout和stderr

    Process process = pb.start();

    // 异步读取输出
    ProcessOutputReader outputReader = new ProcessOutputReader(process.getInputStream());
    ProcessOutputReader errorReader = new ProcessOutputReader(process.getErrorStream());
    outputReader.start();
    errorReader.start();

    // 带超时等待
    boolean finished = process.waitFor(timeoutMinutes, TimeUnit.MINUTES);
    if (!finished) {
      process.destroy();
      throw new TimeoutException("进程执行超时");
    }

    // 等待输出读取完成
    outputReader.join(1000);
    errorReader.join(1000);

    return new ProcessResult(
            process.exitValue(),
            outputReader.getOutput(),
            errorReader.getOutput()
    );
  }

  /**
   * 进程输出读取线程
   */
  private static class ProcessOutputReader extends Thread {
    private final InputStream inputStream;
    private String output;

    ProcessOutputReader(InputStream inputStream) {
      this.inputStream = inputStream;
    }

    @Override
    public void run() {
      try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
        output = reader.lines().collect(Collectors.joining("\n"));
      } catch (IOException e) {
        output = "无法读取进程输出: " + e.getMessage();
      }
    }

    String getOutput() {
      return output;
    }
  }

  /**
   * 进程执行结果
   */
  private static class ProcessResult {
    private final int exitCode;
    private final String standardOutput;
    private final String errorOutput;

    ProcessResult(int exitCode, String standardOutput, String errorOutput) {
      this.exitCode = exitCode;
      this.standardOutput = standardOutput;
      this.errorOutput = errorOutput;
    }

    int getExitCode() {
      return exitCode;
    }

    String getStandardOutput() {
      return standardOutput;
    }

    String getErrorOutput() {
      return errorOutput;
    }
  }
}
