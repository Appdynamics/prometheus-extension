# AppDynamics Integration with Prometheus


## Introduction

This extension connects to a Prometheus endpoint and runs the specified queries.
Responses are then parsed and then passed to [AppDynamics](https://www.appdynamics.com) as analytics events.
[AWS AMP](https://us-west-2.console.aws.amazon.com/prometheus/home) (Amazon Managed Service for Prometheus) is supported using the [AWS Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html) signing process.




## Pre-requisites

1. Java JRE 1.8 or above

2. (Optional) AWS Account with access to an AMP Wokspaces - only required if accessing AMP

3. AppDynamics controller with appropriate Analytics licence.



## Installation

### Clone package

```
$ git clone https://github.com/james201010/prometheus-appd.git
$ cd prometheus-app
```

## Configuration

### Configure primary parameters for extension

Open the the conf/config.yaml file for editing. The default configuration is below

```
!!com.appdynamics.cloud.prometheus.config.ServiceConfig

debugLogging: false
eventsServiceEndpoint: "https://analytics.api.appdynamics.com:443"
eventsServiceApikey: ""
controllerGlobalAccount: ""
prometheusUrl: "https://aps-workspaces.us-west-2.amazonaws.com/workspaces/ws-xxxx-yyy-xxxx-yyy/api/v1/query"
authenticationMode: "awssigv4"    # options are ( none | awssigv4 )
awsRegion: "us-west-2"            # mandatory if authenticationMode = awssigv4
awsAccessKey: ""                  # mandatory if authenticationMode = awssigv4
awsSecretKey: ""                  # mandatory if authenticationMode = awssigv4


# events sources configuration

analyticsEventsSources:

  # see details for events sources configuration in the next section

```


Parameter | Function | Default Value
--------- | -------- | -------------
debugLogging | Choose to turn on debug level logging. | `false`
eventsServiceEndpoint | URL to connect to the AppDynamics controller events service. See [our documentation](https://docs.appdynamics.com/display/PRO45/Analytics+Events+API#AnalyticsEventsAPI-AbouttheAnalyticsEventsAPI) for the URL for your controller. | (blank)
eventsServiceApikey | API Key to connect to AppDynamics controller events service. See [our documentation](https://docs.appdynamics.com/display/PRO45/Managing+API+Keys) to create an API key. | (blank)
controllerGlobalAccount | Account name to connect to the AppDynamics controller. See Settings > License > Account for the value for your controller | (blank)
prometheusUrl | The URL of your Prometheus deployment | `http://localhost:9090/api/v1/query`
authenticationMode | The authentication mode needed to connect to the Prometheus deployment. The options are `none` or `awssigv4` | `none`
awsRegion | The AWS region where your AMP workspace is located (optional if `authenticationMode` is not set to `awssigv4`) | (blank)
awsAccessKey | The access key for the AWS IAM user with access to the AMP workspace (optional if `authenticationMode` is not set to `awssigv4`) | (blank)
awsSecretKey | The secret key for the AWS IAM user with access to the AMP workspace (optional if `authenticationMode` is not set to `awssigv4`) | (blank)
analyticsEventsSources | The list of sources that define the PromQL queries and their associated schema where the metrics from the queries will be published to | one source for `prom_node_metrics` and one for `prom_kubelet_metrics`

### Configure event sources for extension

You can configure 1 to N events sources under the `analyticsEventsSources` parameter.  The default configuration has two events sources defined.  You must have at least one events source defined but can add as many as you like.

This extension has been designed to execute the defined events sources in parallel.

```
# events sources configuration

analyticsEventsSources:
  - schemaName: "prom_node_metrics"
    schemaDefinitionFilePath: "./conf/prom_node_metrics_schema.txt"
    queriesTextFilePath: "./conf/prom_node_metrics_queries.txt"
    eventsSourceClass: "com.appdynamics.cloud.prometheus.analytics.PrometheusEventsSource"
    executionInterval: "1"  # executionInterval (is in minutes, whole numbers only)

  - schemaName: "prom_kubelet_metrics"
    schemaDefinitionFilePath: "./conf/prom_kubelet_metrics_schema.txt"
    queriesTextFilePath: "./conf/prom_kubelet_metrics_queries.txt"
    eventsSourceClass: "com.appdynamics.cloud.prometheus.analytics.PrometheusEventsSource"
    executionInterval: "1"  # executionInterval (is in minutes, whole numbers only)
```


Parameter | Function | Default Value(s)
--------- | -------- | -------------
schemaName | Reporting data to analytics requires a schema to be created. | `prom_node_metrics` and `prom_kubelet_metrics`
schemaDefinitionFilePath | The path to the file that contains the definition of the schema. | `./conf/prom_node_metrics_schema.txt` and `./conf/prom_kubelet_metrics_schema.txt`
queriesTextFilePath | The path to the file that contains the PromQL queries related to the schema. | `./conf/prom_node_metrics_queries.txt` and `./conf/prom_kubelet_metrics_queries.txt`
eventsSourceClass | The fully qualified class name of the events source used to produce events containing Prometheus metrics| (do not change)
executionInterval | The interval in minutes that the PromQL queries will be executed | `1`

### Configure a Schema for an event source

To be able to publish Prometheus data to AppDynamics, a custom schema needs to be created in your controller for each event source. The schema must match the data types of your Prometheus data. The two default event sources configurations (`prom_node_metrics` and `prom_kubelet_metrics`) each have a schema definition file (`./conf/prom_node_metrics_schema.txt` and `./conf/prom_kubelet_metrics_schema.txt`) that matches the data returned from the queries in their associated queries text files `./conf/prom_node_metrics_queries.txt` and `./conf/prom_kubelet_metrics_queries.txt`.

Let's use the `prom_node_metrics` events source configuration in this example.

Open the `./conf/prom_node_metrics_schema.txt` file for editing.

```
metric_name,string
instance,string
job,string
unit,string
kubernetes_node,string
metric_value,float
```

Use the following guidelines when defining the schema:

* Define an attribute for each value `you want to capture` that is returned from at least one Prometheus query.
* If you define an attribute that does not exist in the results of one or more of the queries, the missing attribute will be populated with `null` in the published event.
* If you do not define an atrribute for a specific attribute in the query results, it simply will not be collected.
* The `metric_name` attribute is required and should not be changed.
* The `metric_value` attribute is required and should not be changed.
* Each schema attribute definition should be on its own line with the attribute name first, a comma, and then the data type ( valid data types = boolean, date, float, integer, string).


This extension cannot modify or delete existing schemas. If you have an existing schema which needs editing follow instructions [in our documentation](https://docs.appdynamics.com/display/PRO45/Analytics+Events+API#AnalyticsEventsAPI-update_schemaUpdateEventSchema)


### Configure Prometheus Queries for an event source

The extension has been designed to run Prometheus queries in series for each event source. 

Let's use the `prom_node_metrics` events source configuration in this example.

Open the `./conf/prom_node_metrics_queries.txt` file for editing.

The extension will run the three sample queries as defined in this file and send the data to AppDynamics as analytics events.

```
node_network_receive_bytes_total
node_disk_read_bytes_total
kube_node_status_capacity
```

The three default queries in this file are listed above. You can add and change these to match the data that you'd like to export from Prometheus to AppDynamics. Each query should be on its own line.

Once you have added your queries you should ensure that your schema config matches the data that Prometheus will return (using the schema definition guidelines mentioned earlier). Failure to do this will cause an error at runtime.


## Run Extension

Change to the directory where you have installed the extension (e.g. `prometheus-app`)

```
$ ./run_service.sh
```



