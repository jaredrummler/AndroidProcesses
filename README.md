# AndroidProcesses

> **No longer maintained.**  
> As of Android 7.0+, Google blocks the required /proc access for non-system apps.  
> If you're targeting modern Android, stop here. For historical, forensic, or academic use, read on.

---

## TL;DR

AndroidProcesses enabled process enumeration on Android without special permissions by parsing `/proc`.  
This approach worked up through Android 6.0 (Marshmallow).  
Since Android 7.0 (Nougat), `/proc` is inaccessible to third-party apps, rendering this library ineffective on new devices.

If your work involves legacy device research, system forensics, or historical Android analysis, read on.  
Otherwise, note the platform’s current constraints.

---

## Project Overview

AndroidProcesses offered a simple interface to enumerate all running processes and gather details such as PID, memory use, parent/child relationships, and more, with zero special permissions. It was widely adopted by utility apps (e.g., ES File Explorer, Clean Master, Cheetah Mobile products), amassing billions of installs and becoming a standard tool for both system utilities and academic research.

---

## Impact in Research and Security

AndroidProcesses was widely referenced in peer-reviewed security, privacy, and systems research, including:

- Security and privacy analyses of Android’s process model and sandboxing
- Demonstrations of UI deception and side-channel attack feasibility
- Forensic analysis, malware detection, and runtime system monitoring
- Motivation and justification for major platform policy changes

Numerous scholarly articles, theses, and dissertations (see below) used this library as their practical method of process enumeration and system introspection.

---

## Why was access restricted?

Android 7.0’s restriction of `/proc` access was motivated by the desire to:
- Improve application sandboxing and user privacy
- Prevent background apps from surveilling user activities and other processes
- Close system-level attack and data leak vectors revealed by research (often using AndroidProcesses)

The trade-off: reduced process transparency and visibility for security researchers, system analysts, forensics, and device owners.

---

## Usage (for legacy devices)

```java
List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
for (AndroidAppProcess process : processes) {
  String processName = process.name;
  Stat stat = process.stat();
  int pid = stat.getPid();
  // ...etc...
}
```
*Functional on Android ≤6.0. On 7.0+, returns nothing due to system restrictions.*

---

## Alternatives

- [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager): Permission-gated, delayed, limited to usage statistics.
- Accessibility Service: Not designed for process monitoring; limited and discouraged for this use case.
- Custom ROMs, rooted devices, or enterprise device management: Required for deep system introspection on modern Android.

---

## Adoption

Major adoption between 2015–2018 by ES File Explorer, Clean Master, Security Master, CM Launcher 3D, Virus Cleaner, Super Cleaner, and many more.  
If you analyze utility apps or OEM tools from that period, you will likely encounter AndroidProcesses.

---

## Academic References (selected & annotated)

If referencing this library in academic work, we recommend the following citation format:

**BibTeX:**
```bibtex
@software{androidprocesses,
  author = {Rummler, Jared},
  title = {AndroidProcesses},
  year = {2015},
  publisher = {GitHub},
  url = {https://github.com/jaredrummler/AndroidProcesses}
}
```

**APA:**  
Rummler, J. (2015). AndroidProcesses [Software]. https://github.com/jaredrummler/AndroidProcesses

**Selected scholarly citations:**

- **Tuncay, G. S., Qian, J., & Gunter, C. A. (2020).**  
  _See No Evil: Phishing for Permissions with False Transparency_  
  [USENIX Security 2020](https://www.usenix.org/conference/usenixsecurity20/presentation/tuncay)  
  *Used AndroidProcesses to demonstrate UI deception and permission phishing.*

- **Bianchi, A., et al. (2015).**  
  _What the App is That? Deception and Countermeasures in the Android User Interface_  
  IEEE S&P 2015.  
  *Referenced AndroidProcesses as a tool for studying UI deception attacks.*

- **Spreitzer, R., Kirchengast, F., Gruss, D., & Mangard, S. (2018).**  
  _ProcHarvester: Fully Automated Analysis of Procfs Side-Channel Leaks on Android_  
  [ASIACCS 2018](https://doi.org/10.1145/3196494.3196510)  
  *AndroidProcesses was cited for its practical relevance in analyzing side-channel leaks.*

- **Simon, L., Xu, W., & Anderson, R. (2016).**  
  _Don’t Interrupt Me While I Type: Inferring Text Entered Through Gesture Typing on Android Keyboards_  
  [PoPETs 2016](https://doi.org/10.1515/popets-2016-0020)  
  *Showed use of process enumeration for real-world side-channel attacks.*

- **Chang, M. (2018).**  
  _Predicting mobile application power consumption._  
  [UOIT Thesis](http://hdl.handle.net/10155/934)

- **Dorotík, L. (2020).**  
  _Detection of Mobile Malware Based on Signatures of Nontrivial Features_  
  [UTB Thesis](http://hdl.handle.net/10563/47856)

- **Baresi, L., & Caushi, K. (2021).**  
  _IDEA: Runtime Collection of Android Data_  
  [ISSREW 2021](https://doi.org/10.1109/ISSREW53611.2021.00055)

- **Bianchi, A. (2018).**  
  _Identifying and Mitigating Trust Violations in the Mobile Ecosystem_  
  [UCSB Dissertation](https://escholarship.org/uc/item/60k610h0)

- **Muthu, S. (n.d.).**  
  _A Context-Aware Approach to Android Memory Management_  
  [OhioLINK Thesis](http://rave.ohiolink.edu/etdc/view?acc_num=toledo144966550)

- **Simon, L. (2017).**  
  _Exploring new attack vectors for the exploitation of smartphones_  
  [Cambridge TR 909](https://www.cl.cam.ac.uk/techreports/UCAM-CL-TR-909.pdf)

---

## License

Apache 2.0.  
See [LICENSE.txt](LICENSE.txt).

---

## Further Reading

- [Project announcement & postmortem](https://jaredrummler.com/2017/09/13/android-processes/)
- [Google Issue Tracker: /proc restriction](https://code.google.com/p/android/issues/detail?id=205565)
- [Android Developer Docs: UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager)

**Donations:**  
`bc1qm33s8s0hh5dqffpvv5ahmgyte2sy7g7pjftl43` 🖖

---

Android moved on. So should your app.

Yet as the ecosystem evolves, the importance of transparency, device sovereignty, and open system research remains.  
For those interested in security, forensics, or platform policy, AndroidProcesses stands as a historical reference point—reminding us that real progress means learning from both the tools we've built and the access we've lost.
