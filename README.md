Sample code for https://github.com/JFrogDev/build-info/issues/116 &amp; https://github.com/JFrogDev/build-info/issues/117

# https://github.com/JFrogDev/build-info/issues/116
You’ll have to download it and fill in a public repoKey, username, & password, as well as a resolve repository repoKey. Also a contextUrl that points to some artifactory location.
 
At the end of the day though I’ve debugged this myself - https://github.com/JFrogDev/build-info/blob/master/build-info-extractor-gradle/src/main/groovy/org/jfrog/gradle/plugin/artifactory/task/helper/TaskHelperPublications.java#L253
 
In `org.jfrog.gradle.plugin.artifactory.task.helper.TaskHelperPublications` in the method `private DeployDetails.Builder createBuilder(File file, String publicationName)`
 
The first thing it does is

```java 
if (!file.exists()) {
  throw new GradleException("File '" + file.getAbsolutePath() + "'" +
                    " does not exist, and need to be published from publication " + publicationName);
}
```
 
If the publication has reference to an artifact which doesn’t exist (because the task that produces it was skipped due to there not being any input to perform the task), then the publish fails. I would think that there should be cases where we should be able to say “include the output of this task in the publication if it exists”, which is the case I have here.
 
I am trying to build an enterprise-wide plugin which enforces certain conventions. If things exist in certain places in the source tree, then publish an artifact containing them. If not, don’t publish them.

# https://github.com/JFrogDev/build-info/issues/117
Right now if your run **gradlew clean build** it will all work fine. If in build.gradle you change
 
`compile 'org.jfrog.buildinfo:build-info-extractor-gradle:4.4.9'`
 
to be
 
`compile 'org.jfrog.buildinfo:build-info-extractor-gradle:4.4.10'`
 
or any other version > 4.4.9
 
and re-run **gradlew clean build**, then it will blow up. There is a single class [TestPlugin.groovy](src/main/groovy/org/mycompany/gradle/TestPlugin.groovy) and a single test class [TestPluginSpec.groovy](src/test/groovy/org/mycompany/gradle/TestPluginSpec.groovy).
 
```
:clean
:compileJava NO-SOURCE
:compileGroovy
:pluginDescriptors
:processResources
:classes
:jar
:assemble
:pluginUnderTestMetadata
:compileTestJava NO-SOURCE
:compileTestGroovy
:processTestResources NO-SOURCE
:testClasses
:test
 
org.mycompany.gradle.TestPluginSpec > Test for ext property FAILED
    org.gradle.api.ProjectConfigurationException: A problem occurred configuring root project 'project'.
        at org.gradle.configuration.project.LifecycleProjectEvaluator.addConfigurationFailure(LifecycleProjectEvaluator.java:94)
        at org.gradle.configuration.project.LifecycleProjectEvaluator.doConfigure(LifecycleProjectEvaluator.java:64)
        at org.gradle.configuration.project.LifecycleProjectEvaluator.access$000(LifecycleProjectEvaluator.java:33)
        at org.gradle.configuration.project.LifecycleProjectEvaluator$1.execute(LifecycleProjectEvaluator.java:53)
        at org.gradle.configuration.project.LifecycleProjectEvaluator$1.execute(LifecycleProjectEvaluator.java:50)
        at org.gradle.internal.Transformers$4.transform(Transformers.java:169)
        at org.gradle.internal.progress.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:106)
        at org.gradle.internal.progress.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:61)
        at org.gradle.configuration.project.LifecycleProjectEvaluator.evaluate(LifecycleProjectEvaluator.java:50)
        at org.gradle.api.internal.project.DefaultProject.evaluate(DefaultProject.java:648)
        at org.gradle.api.internal.project.DefaultProject.evaluate(DefaultProject.java:126)
        at org.gradle.api.internal.project.DefaultProject$3.execute(DefaultProject.java:775)
        at org.gradle.api.internal.project.DefaultProject$3.execute(DefaultProject.java:772)
        at org.gradle.api.internal.project.DefaultProject.getTasksByName(DefaultProject.java:786)
        at org.jfrog.gradle.plugin.artifactory.extractor.listener.ProjectsEvaluatedBuildListener.afterEvaluate(ProjectsEvaluatedBuildListener.groovy:44)
        at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
        at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
        at org.gradle.internal.event.DefaultListenerManager$ListenerDetails.dispatch(DefaultListenerManager.java:305)
        at org.gradle.internal.event.DefaultListenerManager$ListenerDetails.dispatch(DefaultListenerManager.java:285)
        at org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:58)
        at org.gradle.internal.event.DefaultListenerManager$EventBroadcast$ListenerDispatch.dispatch(DefaultListenerManager.java:273)
        at org.gradle.internal.event.DefaultListenerManager$EventBroadcast$ListenerDispatch.dispatch(DefaultListenerManager.java:260)
        at org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:42)
        at org.gradle.internal.event.BroadcastDispatch$SingletonDispatch.dispatch(BroadcastDispatch.java:221)
        at org.gradle.internal.event.BroadcastDispatch$SingletonDispatch.dispatch(BroadcastDispatch.java:145)
        at org.gradle.internal.event.ListenerBroadcast.dispatch(ListenerBroadcast.java:138)
        at org.gradle.internal.event.ListenerBroadcast.dispatch(ListenerBroadcast.java:35)
        at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
        at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
        at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
        at org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:42)
        at org.gradle.internal.event.BroadcastDispatch$SingletonDispatch.dispatch(BroadcastDispatch.java:221)
        at org.gradle.internal.event.BroadcastDispatch$SingletonDispatch.dispatch(BroadcastDispatch.java:145)
        at org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:58)
        at org.gradle.internal.event.BroadcastDispatch$CompositeDispatch.dispatch(BroadcastDispatch.java:315)
        at org.gradle.internal.event.BroadcastDispatch$CompositeDispatch.dispatch(BroadcastDispatch.java:225)
        at org.gradle.internal.event.ListenerBroadcast.dispatch(ListenerBroadcast.java:138)
        at org.gradle.internal.event.ListenerBroadcast.dispatch(ListenerBroadcast.java:35)
        at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
        at org.mycompany.gradle.TestPluginSpec.runProjectAfterEvaluate(TestPluginSpec.groovy:27)
        at org.mycompany.gradle.TestPluginSpec.Test for ext property(TestPluginSpec.groovy:36)
 
        Caused by:
        java.lang.IllegalStateException: Cannot notify listeners of type ProjectEvaluationListener as these listeners are already being notified.
            at org.gradle.internal.event.DefaultListenerManager$EventBroadcast.startNotification(DefaultListenerManager.java:201)
            at org.gradle.internal.event.DefaultListenerManager$EventBroadcast.access$300(DefaultListenerManager.java:123)
            at org.gradle.internal.event.DefaultListenerManager$EventBroadcast$ListenerDispatch.dispatch(DefaultListenerManager.java:270)
            at org.gradle.internal.event.DefaultListenerManager$EventBroadcast$ListenerDispatch.dispatch(DefaultListenerManager.java:260)
            at org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:42)
            at org.gradle.internal.event.BroadcastDispatch$SingletonDispatch.dispatch(BroadcastDispatch.java:221)
            at org.gradle.internal.event.BroadcastDispatch$SingletonDispatch.dispatch(BroadcastDispatch.java:145)
            at org.gradle.internal.event.ListenerBroadcast.dispatch(ListenerBroadcast.java:138)
            at org.gradle.internal.event.ListenerBroadcast.dispatch(ListenerBroadcast.java:35)
            at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
            at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
            at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
            at org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:42)
            at org.gradle.internal.event.BroadcastDispatch$SingletonDispatch.dispatch(BroadcastDispatch.java:221)
            at org.gradle.internal.event.BroadcastDispatch$SingletonDispatch.dispatch(BroadcastDispatch.java:145)
            at org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:58)
            at org.gradle.internal.event.BroadcastDispatch$CompositeDispatch.dispatch(BroadcastDispatch.java:315)
            at org.gradle.internal.event.BroadcastDispatch$CompositeDispatch.dispatch(BroadcastDispatch.java:225)
            at org.gradle.internal.event.ListenerBroadcast.dispatch(ListenerBroadcast.java:138)
            at org.gradle.internal.event.ListenerBroadcast.dispatch(ListenerBroadcast.java:35)
            at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
            at org.gradle.configuration.project.LifecycleProjectEvaluator.doConfigure(LifecycleProjectEvaluator.java:62)
            ... 39 more
 
1 test completed, 1 failed
:test FAILED
 
FAILURE: Build failed with an exception.
 
* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///Users/edeandre/workspaces/Stuff/artifactory-issue-116/build/reports/tests/test/index.html
 
* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.
 
BUILD FAILED
 
Total time: 4.61 secs
```
