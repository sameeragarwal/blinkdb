from django.conf.urls.defaults import *

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Example:
     # (r'^blinkdb/', include('blinkdb.foo.urls')),
     # (r'^blinkdb/', include('blinkdb.views.ajax_example')),
     (r'^blinkdb/', 'blinkdb.views.blinkdb_console'),

    # Uncomment the admin/doc line below to enable admin documentation:
    # (r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # (r'^admin/', include(admin.site.urls)),
)
