## IQCareCompanion [![Build Status](https://travis-ci.org/tedb19/IQCareCompanion.svg)](https://travis-ci.org/tedb19/IQCareCompanion) [![Coverage Status](https://coveralls.io/repos/tedb19/IQCareCompanion/badge.svg?branch=master&service=github)](https://coveralls.io/github/tedb19/IQCareCompanion?branch=master)

A HIV Case Based Surveillance System companion for IQCare (https://fgiqcare.codeplex.com/). 

Major role is to generate hl7 messages for any **sentinel event** of interest registered in the EMR, in a real-time fashion.

Before deployment, ensure the values in the properties files are well set.

Sentinel events to be captured have to be defined in the *events.json* file.

To mine all the sentinel events within the EMR, simply reset the values in *runtime.properties* to 0.