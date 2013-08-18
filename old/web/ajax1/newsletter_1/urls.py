#!/usr/bin/env python
# encoding: utf-8
from django.conf.urls.defaults import *
from views import *

urlpatterns = patterns('',
    url(r'^start$', index, name="index"),
    url(r'^newsletter_add$', newsletter_add, name="newsletter_add"),
)