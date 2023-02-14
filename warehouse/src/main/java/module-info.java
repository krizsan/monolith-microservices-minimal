module warehouse {
    requires lombok;
    requires jakarta.persistence;
    requires jakarta.transaction;

    requires spring.beans;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.data.jpa;
    requires org.hibernate.orm.core;
    requires spring.core;

    exports se.ivankrizsan.monolithmicroservices.warehouse.api;
    exports se.ivankrizsan.monolithmicroservices.warehouse.configuration;

    opens se.ivankrizsan.monolithmicroservices.warehouse.configuration to spring.core;
    opens se.ivankrizsan.monolithmicroservices.warehouse.implementation to spring.core;
    opens se.ivankrizsan.monolithmicroservices.warehouse.domain;
}