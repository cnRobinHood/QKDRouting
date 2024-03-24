package QRNGUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class QuantumRandomNumber {
    public static int getQuantumRandomCode(int range) {
        return Integer.parseInt(executePythonCode("/Users/liu/IdeaProjects/QKD-Routing/src/QuantumRandomNumber.py", range));
    }

    //通过python调用IBM qiskit去获取生成的量子随机数
    public static String executePythonCode(String pythonCode, int range) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonCode, range + "");
            // 启动 Python 进程
            Process process = processBuilder.start();
            // 等待 Python 进程执行完毕，并获取其输出结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            reader.close();
            int exitCode = process.waitFor();
            // 检查 Python 进程是否成功结束
            if (exitCode != 0) {
                throw new RuntimeException("Python 进程执行失败，退出码：" + exitCode);
            }
            return result.toString().trim();
        } catch (IOException e) {
            throw new RuntimeException("执行 Python 代码时发生错误：" + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException("等待 Python 进程结束时被中断");
        }
    }
}
