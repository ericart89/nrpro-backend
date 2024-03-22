# NRPro-backend

This is the back-end service for [NRPro](https://bitbucket.org/sib-pig/rban/src/master/). It's a
Java API that can be packaged in a war file and deployed by the Tomcat server. The Dockerfile included in the project
already contains tomcat. The container uses the file _tomcat_conf/web.xml_ to change its
configuration in order to adapt the CORS filters.

The project is a git submodule of [NRPro-webapp](https://bitbucket.org/sib-pig/rban/src/master/) where all the
components of the application can be deployed with docker-compose.

## Deployment

First create the image:

```bash
sudo docker build -t [IMAGE NAME] .
```
Then run it and specify the port where it will be hosted (both, locally and in the container):

```bash
sudo docker run -p 8080:8080 [IMAGE NAME]
```

## Rebuild WAR file

The project already includes the war file needed for Tomcat (nrpro-backend/target/nrpro-backend.war). However,
if necessary, it can be re-build using Maven:

```bash
mvn install
```

## Citation

Ricart, E., Pupin, M., Müller, M. et al. Automatic annotation and dereplication of tandem mass spectra of peptidic natural products.
https://pubs.acs.org/doi/10.1021/acs.analchem.0c03208

## License

[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International Public License](https://creativecommons.org/licenses/by-nc-sa/4.0/)
