# java-precommit-hook-week8-practice1

**Codebase:** This script is written in Java

**Purpose:** The general idea here, is to install `gitleaks` utility based on user's OS/ARCH and check if there are any secrets hardcoded in staged for commit files.

**Execution:**  

- Script is running `./gitleaks protect --staged . -v` in your local git repo
- In case script detects that `gitleak` is not installed, it executes the installation process
- This script also adds `pre-commit` hook to current local git repo
- In addition it adds "git config gitleaks.enable true" to `git config` upon the `gitleak` utility installation
- You can manually add `git config gitleaks.enable false` to disable this script from execution, `gitleaks` utility will not be triggered and user will be prompted about that!

**How to run it (no IDE)?**

- download JDK
- execute `javac GitLeaks.java` to compile the source file (`.class` file will be created upon execution)
- execute `java GitLeaks` to run the script


**How to run it (using IDE)?**

- open `GitLeaks.java` file in your favourite IDE (IntelliJ Idea, VsCode, Eclipse etc.)
- click `Run|Run&Debug` button



***- - - - - - - - - Proof Of Concept - - - - - - - - -***

**using the script itself**
````
 ~/study/devops_course/week8  main +7 !2 ?2  java GitLeaks                                                                                                                                                                                     ✔ 

    ○
    │╲
    │ ○
    ○ ░
    ░    gitleaks

5:09PM INF 1 commits scanned.
5:09PM INF scan completed in 47.5ms
5:09PM WRN leaks found: 2

Finding:     TELE_TOKEN: adcwerkakscnf334r455asd
kind: Secret
Secret:      adcwerkakscnf334r455asd
RuleID:      generic-api-key
Entropy:     3.621176
File:        secret3.yaml
Line:        3
Fingerprint: secret3.yaml:generic-api-key:3

Finding:     TELE_TOKEN: adcwerkakscnf334r455asd
kind: Secret
Secret:      adcwerkakscnf334r455asd
RuleID:      generic-api-key
Entropy:     3.621176
File:        secret4.yaml
Line:        3
Fingerprint: secret4.yaml:generic-api-key:3


Commit rejected, check GitLeaks output above!
````

**using the `pre-commit` hook created by the script**

````
 ~/study/devops_course/week8  main +7 !2 ?2  git commit -m "random message"                                                                                                                                                                  1 ✘ 

    ○
    │╲
    │ ○
    ○ ░
    ░    gitleaks

Finding:     TELE_TOKEN: adcwerkakscnf334r455asd
kind: Secret
Secret:      adcwerkakscnf334r455asd
RuleID:      generic-api-key
Entropy:     3.621176
File:        secret3.yaml
Line:        3
Fingerprint: secret3.yaml:generic-api-key:3

Finding:     TELE_TOKEN: adcwerkakscnf334r455asd
kind: Secret
Secret:      adcwerkakscnf334r455asd
RuleID:      generic-api-key
Entropy:     3.621176
File:        secret4.yaml
Line:        3
Fingerprint: secret4.yaml:generic-api-key:3

5:12PM INF 1 commits scanned.
5:12PM INF scan completed in 19.3ms
5:12PM WRN leaks found: 2
````


**gitleaks is not installed, pre-commit hook is not added**

````
 ~/study/devops_course/week8  main +7 !2 ?2  rm gitleaks .git/hooks/pre-commit                                                                                                                                                               1 ✘ 
 ~/study/devops_course/week8  main +7 !3 ?2  java GitLeaks                                                                                                                                                                                     ✔ 
GitLeaks is not installed, installing ...
Gitleaks_URL: https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_darwin_arm64.tar.gz

    ○
    │╲
    │ ○
    ○ ░
    ░    gitleaks

5:15PM INF 1 commits scanned.
5:15PM INF scan completed in 51.3ms
5:15PM WRN leaks found: 2

Finding:     TELE_TOKEN: adcwerkakscnf334r455asd
kind: Secret
Secret:      adcwerkakscnf334r455asd
RuleID:      generic-api-key
Entropy:     3.621176
File:        secret4.yaml
Line:        3
Fingerprint: secret4.yaml:generic-api-key:3

Finding:     TELE_TOKEN: adcwerkakscnf334r455asd
kind: Secret
Secret:      adcwerkakscnf334r455asd
RuleID:      generic-api-key
Entropy:     3.621176
File:        secret3.yaml
Line:        3
Fingerprint: secret3.yaml:generic-api-key:3


Commit rejected, check GitLeaks output above!

 ~/study/devops_course/week8  main +7 !2 ?2  ls gitleaks; ls .git/hooks/pre-commit                                                                                                                                                             ✔ 
gitleaks
.git/hooks/pre-commit
````
