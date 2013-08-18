from django.db import models
from django.conf import settings
from django import forms
from django.utils.translation import ugettext_lazy as _

class NewsletterForm(forms.Form):
    #email = forms.EmailField(widget=forms.TextInput(attrs=dict(attrs_dict,maxlength=75)),label=_("Adres email"))
    email = forms.EmailField(label=_("Adres email"))