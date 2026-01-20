# Energy-Aware Environments for Cyber-Physical Systems

This repository provides the **source code and experimental dataset** associated with the research work:

**Energy-aware environments for the development of green applications for cyber–physical systems**
*Daniel-Jesus Muñoz, José A. Montenegro, Mónica Pinto, Lidia Fuentes*
Published in **Future Generation Computer Systems**, Volume 91, 2019, Pages 536–554.

📄 **Official publication (institutional repository):**
[https://riuma.uma.es/entities/publication/1a72117c-2ad6-48db-98d9-02fccf087e01](https://riuma.uma.es/entities/publication/1a72117c-2ad6-48db-98d9-02fccf087e01)

📘 **Journal version (ScienceDirect):**
[https://doi.org/10.1016/j.future.2018.09.006](https://doi.org/10.1016/j.future.2018.09.006)

---

## Overview

Cyber–Physical Systems (CPSs) are typically composed of heterogeneous, resource-constrained, and battery-powered devices. Design and implementation decisions at the software level can have a **significant impact on energy consumption** and, consequently, on system lifetime.

This repository accompanies the proposed **Developer Eco-Assistant**, an energy-aware framework that:

* Integrates experimental energy-consumption data into development environments.
* Supports sustainability-aware decision making during CPS software development.
* Extends the HADAS energy repository to account for **device, operating system, and programming language heterogeneity**.
* Provides energy and performance recommendations through IDE plugins and web services.

The code and datasets here allow **reproducibility**, **reuse**, and **extension** of the experimental results presented in the paper.

---

## Repository Contents

The repository is organized as follows:

```
.
├── code/
│   ├── hadas4cps/        # Core logic for HADAS4CPS variability and reasoning
│   ├── microservice/    # HADAS4CPS micro-service for energy queries
│   ├── ide-plugin/      # JetBrains IDE green plugin (prototype)
│   └── experiments/     # Scripts used to run energy and performance measurements
│
├── dataset/
│   ├── android/         # Energy measurements for Android devices (PowerTutor-based)
│   ├── waspmote/        # Hardware-based energy measurements for Waspmote
│   ├── cryptography/   # AES and RSA energy/time datasets
│   └── communication/  # Bluetooth and WiFi energy/time datasets
│
├── docs/
│   ├── paper.pdf        # PDF of the published article
│   └── figures/         # Supporting figures and diagrams
│
└── README.md
```

> **Note:** Folder names may slightly vary depending on the version of the artifact. Please refer to the inline documentation within each directory.

---

## Dataset Description

The dataset includes **energy and execution-time measurements** obtained from both **software-based** and **hardware-based** profiling platforms:

### Platforms

* **Android smartphones** (e.g., Nexus One)

  * Energy measured using a modified version of **PowerTutor**.
* **Waspmote PRO v1.2**

  * Energy measured using an **Arduino-based power measurement shield**.

### Evaluated Concerns (ECCs)

* **Security (Cryptography)**

  * AES (128, 192, 256 bits)
  * RSA (512-bit keys)
  * Multiple modes, paddings, and data sizes
* **Communication**

  * Bluetooth
  * WiFi

Each dataset entry typically includes:

* Device
* Operating system / firmware
* Programming language
* Algorithm and configuration parameters
* Execution time (ms)
* Energy consumption (mJ)

The datasets are provided in **CSV format** to facilitate reuse and integration into other analysis pipelines.

---

## Code Usage

The provided code allows you to:

1. Extend or modify the **HADAS4CPS variability model**.
2. Query the **energy-efficiency repository** using the HADAS4CPS micro-service.
3. Reproduce the **sustainability analyses** shown in the paper.
4. Integrate energy-awareness into development workflows via IDE support.

Detailed build and execution instructions are available in the corresponding subdirectories.

---

## Reproducibility

All experiments reported in the paper can be reproduced using:

* The scripts provided in `code/experiments/`
* The datasets under `dataset/`
* The configuration parameters described in the paper (Section 6)

Hardware-specific measurements may show minor variations due to environmental conditions; however, **relative trends and conclusions are consistent**.

---

## Citation

If you use this code or dataset in your research, please cite the following work:

```bibtex
@article{Munoz2019FGCS,
  title   = {Energy-aware environments for the development of green applications for cyber--physical systems},
  author  = {Mu{\~n}oz, Daniel-Jesus and Montenegro, Jos{\'e} A. and Pinto, M{\'o}nica and Fuentes, Lidia},
  journal = {Future Generation Computer Systems},
  volume  = {91},
  pages   = {536--554},
  year    = {2019},
  doi     = {10.1016/j.future.2018.09.006}
}
```

---

## License

This repository is intended for **research and educational purposes**.

Unless otherwise stated, the code and datasets are released under the **MIT License**. See the `LICENSE` file for details.

---

## Contact

For questions, feedback, or collaboration inquiries:

* **José A. Montenegro**
  University of Málaga (UMA)
  Email: [monte@lcc.uma.es](mailto:monte@lcc.uma.es)

---

*This repository aims to promote reproducible and energy-aware software engineering practices for Cyber–Physical Systems.*
