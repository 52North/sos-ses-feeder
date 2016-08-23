# General Infos

The SOS-SES-Feeder acts as an [SOS](https://github.com/52North/SOS/)
to [SES](https://github.com/52North/SES/) feeding bridge. It creates a
notification stream for a Sensor Event Service based on the standardized
[OGC SOS interface](http://www.opengeospatial.org/standards/sos).

Currently only [SOS standard version 1.0.0](http://www.opengeospatial.org/standards/sos)
is supported.

## Configuration

Th project is quite small, so here are the locations which can
be modified to configure the application:

  - hibernate.cfg.xml (database settings)
  - configuration.xml (feeder settings)

Add/Change the SOS instance in the SOS table once the application has
been deployed in a servlet container (e.g. tomcat). Uncomment the
hibernate property `hibernate.hbm2ddl.auto` to create database schema.

