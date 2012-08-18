from django.db import models
from django.conf import settings
from django.utils.translation import ugettext_lazy as _

class NewsletterEmails(models.Model):
    email = models.EmailField(_(u"Adres e-mail"),)