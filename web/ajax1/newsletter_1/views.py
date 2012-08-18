from django.db import models
from django.conf import settings
from django.utils.translation import ugettext_lazy as _
from django.http import HttpResponseRedirect, Http404
from django.template import Context, RequestContext
from django.shortcuts import render_to_response, get_object_or_404

from django.http import HttpResponse
from django.template.loader import render_to_string
from django.utils import simplejson
from django.utils.functional import Promise
from django.utils.encoding import force_unicode
from models import NewsletterEmails
from forms import NewsletterForm

class LazyEncoder(simplejson.JSONEncoder):
    """Encodes django's lazy i18n strings.
    """
    def default(self, obj):
        if isinstance(obj, Promise):
            return force_unicode(obj)
        return obj

def newsletter_add(request):
    if request.method == "POST":       
        adr = request.POST['email']
        e = None
        if len(adr) > 6:
            form = NewsletterForm(data=request.POST)
            if form.is_valid():
                try:
                    e = NewsletterEmails.objects.get(email = adr)
                    message = _(u"Email already added.")
                    type = "error"
                except NewsletterEmails.DoesNotExist:
                    try:
                        e = NewsletterEmails(email = adr)
                    except DoesNotExist:
                        pass
                    message = _(u"Email added.")
                    type = "success"
                    e.save()
            else:
                message = _(u"Bad address.")
                type = "error"                
        else:
            message = _(u"Too short address.")
            type = "error"

    if request.is_ajax():
        result = simplejson.dumps({
            "message": message,
            "type": type,
        }, cls=LazyEncoder)
        return HttpResponse(result, mimetype='application/javascript')
        
def index(request):
    template = 'newsletter/newsletter1.html'
    html = render_to_string(template, RequestContext(request, {}))            
    return HttpResponse(html)    