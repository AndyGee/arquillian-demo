# @RunWith(Arquillian.class) - "Production Near" JavaEE Testing

### Summary 
Arquillian driven testing, and the advantages it brings in regards to ensuring application stability, is becoming more widely known. If you are not already using Arquillian then you are likely to be testing yourself rather than your application. If you are, then you might be interested in something new - Arquillian meets Docker!

### Type of talk 
Java, Technical - Testing JavaEE Applications.

### Audience level
Beginner to intermediate JavaEE developers.

### Type
Slides and Code

### Abstract
Arquillian driven testing is fast becoming a de-facto standard for testing JavaEE applications. It builds upon the solid and well established foundation of JUnit or TestNG by pulling in the "Production Near" runtime of your chosen application server. The advantages of this should be clear - It is like letting your test case out of the box, but without the risk of pulling your actual production environment down.

The goals of Arquillian are very clear:

 - Tests should be portable to any supported container.
 - Tests should be executable from both the IDE and the build tool.
 - The platform should extend or integrate existing test frameworks.

This all comes with "out of the box" support for in-test CDI, which will greatly enhance your testing scenarios.

A huge collection of plugin modules are available to further extend virtually every aspect of JavaEE testing.

One of the latest features to hit the ground running is "Arquillian Cube" - An Arquillian extension that can be used to manage Docker containers directly from Arquillian. Get more closer to the production environment than ever before!

This talk will take the Arquillian beginner from the very basic first steps of production near testing using Arquillian, CDI and Apache TomEE and enable them to join the advanced user on the path to using Arquillian Cube.