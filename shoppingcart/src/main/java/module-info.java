module shoppingcart {
    requires lombok;
    requires jakarta.persistence;
    requires jakarta.transaction;

    requires spring.core;
    requires spring.beans;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.data.jpa;

    requires transitive warehouse;

    exports se.ivankrizsan.monolithmicroservices.shoppingcart.api;
    exports se.ivankrizsan.monolithmicroservices.shoppingcart.configuration;

    opens se.ivankrizsan.monolithmicroservices.shoppingcart.configuration to spring.core;
    opens se.ivankrizsan.monolithmicroservices.shoppingcart.domain to spring.core, org.hibernate.orm.core;
    opens se.ivankrizsan.monolithmicroservices.shoppingcart.implementation to spring.core;
}