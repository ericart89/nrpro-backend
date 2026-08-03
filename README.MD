# NRPro Backend

This is the back-end service for [NRPro](https://bitbucket.org/sib-pig/nrpro-private/src/master/). It's a
Java API that can be packaged in a war file and deployed by the Tomcat server. The Dockerfile included in the project
already contains tomcat. The container uses the file _tomcat_conf/web.xml_ to change its
configuration in order to adapt the CORS filters.

The project is a git submodule of [NRPro-webapp](https://bitbucket.org/sib-pig/nrpro-webapp/src/master/) where all the
components of the application can be deployed with docker-compose.
=======
The **NRPro Backend** implements the core annotation engine, business logic, and REST services of the **NRPro** research software platform. It processes tandem mass spectra, performs annotation and dereplication against the NRPro database, and exposes the functionality through a Java-based API.

The backend is developed in Java and packaged as a WAR application for deployment with Apache Tomcat. The included Dockerfile provides a complete Tomcat-based deployment environment, while `tomcat_conf/web.xml` configures the required CORS filters.

This repository is included as a Git submodule of the **NRPro Webapp**, which orchestrates the complete platform (frontend, backend, and database) using Docker Compose.

## Related resources

- **Live application:** https://norine.univ-lille.fr/nrpro/
- **Publication:** https://doi.org/10.1021/acs.analchem.0c03208
- **Main project:** https://github.com/ericart89/nrpro-webapp

---


## Deployment

Build the Docker image:

```bash
docker build -t <image-name> .
```

Run the container and expose the application on port 8080:

```bash
docker run -p 8080:8080 <image-name>
```

---

## Building from source

Build the project using Maven:

```bash
mvn clean package
```

The generated WAR file will be created in:

```text
target/nrpro-backend.war
```

This artifact can then be deployed with Apache Tomcat or used by the provided Docker configuration.

---

## Citation

<<<<<<< HEAD
Ricart, E., Pupin, M., M�ller, M. et al. Automatic annotation and dereplication of tandem mass spectra of peptidic natural products.
https://pubs.acs.org/doi/10.1021/acs.analchem.0c03208
=======
If you use NRPro in your research, please cite:

Ricart, E., Pupin, M., Müller, M., & Lisacek, F.

**Automatic Annotation and Dereplication of Tandem Mass Spectra of Peptidic Natural Products.**

*Analytical Chemistry*, **92**(24), 15862–15871 (2020).

https://doi.org/10.1021/acs.analchem.0c03208

---

## License

Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)

---

## Project status

This repository contains the backend component of the **NRPro** research software platform developed during my PhD research. It is preserved as the reference implementation accompanying the published work and remains available for reproducibility, reuse, and educational purposes.
