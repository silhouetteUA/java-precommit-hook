import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GitLeaks   {

    private static final String GITLEAKS_VERSION = "8.18.0";
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase().trim();  // mac os x
    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase().trim();  // aarch64
    
    public static void main(String[] args) {
        checkUsingGitLeaks();
    }


    private static void installGitLeaks() {

        String FILENAME = "";
                //https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_darwin_arm64.tar.gz
                //https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_darwin_x64.tar.gz
                //https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_linux_arm64.tar.gz
                //https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_linux_x64.tar.gz
                //https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_windows_x64.zip

        if (OS_NAME.contains("linux")) {
            FILENAME += OS_ARCH.contains("aarch64") ? "linux_arm64.tar.gz" : "linux_x64.tar.gz";
        } else if (OS_NAME.contains("mac") || OS_NAME.contains("darwin")) {
            FILENAME += OS_ARCH.contains("aarch64") ? "darwin_arm64.tar.gz" : "darwin_x64.tar.gz";
        } else if (OS_NAME.contains("wind")) {
            FILENAME += OS_ARCH.contains("aarch64") ? "windows_arm64.zip" : "windows_x64.zip";
        } else {
            System.err.println("Wrong OS .. please install yourself something ordinary ...");
            System.exit(1);
        }
        //System.out.println(FILENAME);
        String DOWNLOAD_URL = "https://github.com/gitleaks/gitleaks/releases/download/v" + GITLEAKS_VERSION + "/gitleaks_" + GITLEAKS_VERSION + "_" + FILENAME;
        System.out.println("Gitleaks_URL: "+DOWNLOAD_URL);
        String resultOutput = "gitleaks";

        try {
            Process downloadProcess = null;
            Process extractProcess = null;
            Process chmodProcess = null;
            Process removeProcess = null;
            //Windows
            if (OS_NAME.contains("wind")) {
                downloadProcess = new ProcessBuilder("curl", "-sfL", DOWNLOAD_URL, "-o", resultOutput + ".zip").start();
                downloadProcess.waitFor();
                extractProcess = new ProcessBuilder("7z", "x", resultOutput + ".zip", "-o", resultOutput).start();
                extractProcess.waitFor();
                removeProcess = new ProcessBuilder("del", resultOutput + ".zip").start();
                removeProcess.waitFor();
            // Linux + MacOS
            } else {
                downloadProcess = new ProcessBuilder("curl", "-sfL", DOWNLOAD_URL, "-o", resultOutput + ".tar.gz").start();
                downloadProcess.waitFor();
                extractProcess = new ProcessBuilder("tar", "-xzf", resultOutput+".tar.gz", resultOutput).start();
                extractProcess.waitFor();
                chmodProcess = new ProcessBuilder("chmod", "+x", resultOutput).start();
                chmodProcess.waitFor();
                removeProcess = new ProcessBuilder("rm", "-f", resultOutput + ".tar.gz").start();
                removeProcess.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        addConfigAndHook();
    }



    private static void addConfigAndHook() {
        //git config gitleaks.enable true
        try {
            Process config = new ProcessBuilder("git", "config", "gitleaks.enable", "true").start();
            config.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        //create a pre-commit hook within the current git-repo to use GITLEAKS in order to check staged files for secrets etc.
        try { 
            Files.write(Paths.get(".git/hooks/pre-commit"), "#!/bin/bash\n./gitleaks protect --staged . -v".getBytes(), StandardOpenOption.CREATE);
         } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
         }
        // add execute privileges to pre-commit hook
        try {
            Process chmodProcess = null;
            chmodProcess = new ProcessBuilder("chmod", "+x", ".git/hooks/pre-commit").start();
            chmodProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    private static void checkUsingGitLeaks() {
        
        try {
            Process versionProcess = new ProcessBuilder("./gitleaks", "version").start();
            versionProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("GitLeaks is not installed, installing ...");
            installGitLeaks();
        }

        ProcessBuilder chekcIfGLEnabled = new ProcessBuilder("git", "config", "--get", "gitleaks.enable");
        try {
            Process process = chekcIfGLEnabled.start();
            process.waitFor();
            // System.out.println(new String(process.getErrorStream().readAllBytes()));
            // System.out.println(new String(process.getInputStream().readAllBytes()));
            String resultError = new String(process.getErrorStream().readAllBytes());
            String resultInput = new String(process.getInputStream().readAllBytes());
            if (resultError.contains("true") || resultInput.contains("true"))   {
                ProcessBuilder processBuilder = new ProcessBuilder("./gitleaks", "protect", "--staged", ".", "-v");
                try {
                    Process processLeaksExecute = processBuilder.start();
                    process.waitFor();
                    System.out.println(new String(processLeaksExecute.getErrorStream().readAllBytes()));
                    System.out.println(new String(processLeaksExecute.getInputStream().readAllBytes()));
    
                    if (processLeaksExecute.exitValue() != 0) {
                        System.err.println("Commit rejected, check GitLeaks output above!");
                        System.exit(1);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                System.out.println("You have manually disabled the check, run \"git config gitleaks.enable true\" to enable the ckecks again! ");
            }
        } catch (IOException | InterruptedException e)  {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
