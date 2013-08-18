import commands

from django.utils import simplejson
from django.shortcuts import render_to_response
from django.template import RequestContext

def blinkdb_console(request):
    if not request.POST:
        return render_to_response('console.html', {}, context_instance=RequestContext(request))
    print request
    xhr = request.GET.has_key('xhr')
    response_dict = {}
    query = request.POST.get('query', "")
    response_dict['Query'] = query
    query_command = "$HIVE_HOME/bin/hive -e \'" + query + "\'"
    (status, output) = commands.getstatusoutput(query_command)
    response_dict['Output'] = output
    if xhr:
        return HttpResponse(simplejson.dumps(response_dict), mimetype='application/javascript')
    return render_to_response('console.html', {'response_dict':response_dict}, context_instance=RequestContext(request))
