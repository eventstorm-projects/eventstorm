# eventstorm

[![Build status](https://github.com/eventstorm-projects/eventstorm/actions/workflows/maven.yml/badge.svg)](https://github.com/eventstorm-projects/eventstorm/actions)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=eu.eventstorm%3Aeventstorm&metric=alert_status)](https://sonarcloud.io/dashboard?id=eu.eventstorm%3Aeventstorm)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=eu.eventstorm%3Aeventstorm&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=eu.eventstorm%3Aeventstorm)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=eu.eventstorm%3Aeventstorm&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=eu.eventstorm%3Aeventstorm)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=eu.eventstorm%3Aeventstorm&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=eu.eventstorm%3Aeventstorm)

Eventstorm provides everything required to build a CRQS/Eventsourcing architecture for an application.

## CQRS
CQRS stands for Command and Query Responsibility Segregation, a pattern that separates read and update operations for a data store. 
Implementing CQRS in your application can maximize its performance, scalability, and security. 
The flexibility created by migrating to CQRS allows a system to better evolve over time and prevents update commands from causing merge conflicts at the domain level.

## Event Sourcing
Instead of storing just the current state of the data in a domain, use an append-only store to record the full series of actions taken on that data. 
The store acts as the system of record and can be used to materialize the domain objects. This can simplify tasks in complex domains, by avoiding the need to synchronize the data model and the business domain, while improving performance, scalability, and responsiveness. 
It can also provide consistency for transactional data, and maintain full audit trails and history that can enable compensating actions.

## SAGA
The Saga design pattern is a way to manage data consistency across microservices in distributed transaction scenarios. 
A saga is a sequence of transactions that updates each service and publishes a message or event to trigger the next transaction step.
If a step fails, the saga executes compensating transactions that counteract the preceding transactions.

# Code of Conduct

This project is governed by the [Eventstorm Code of Conduct](CODE_OF_CONDUCT.adoc). 
By participating, you are expected to uphold this code of conduct.

