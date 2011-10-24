import logging, random, math
from datetime import datetime
from time import mktime
from record import Record
from database import Database
from scipy import stats

class Generator():
    """ Record generator """
    
    def __init__(self, db):
        
        self.logger = logging.getLogger(__name__)
        self.db = db
        
        # Get the country distribution since we don't need to create it again.
        self.country_rv = self.get_country_rv()
        self.error_codes_rv = self.get_error_code_rv()
        self.content_rv = self.get_content_rv()
        
        # A few hash maps for easy access rather than going to the DB
        # again and again.
        self.country_city_rv_dict = {}      # city distribution in each country.
        self.country_os_rv_dict = {}        # OS distribution in each country, do we need it per city?
        self.os_version_rv_dict = {}        # OS version distribution for each type.
        self.os_browser_rv_dict = {}        # Browser distribution for each OS.
        self.browser_version_rv_dict = {}   # Browser version distribution for each type.
        
        # For the time being, we assume that any session time would be in a 
        # 1 year period starting from 1/1/2010
        self.global_session_begin_time = mktime(datetime(2010, 1, 1, 0, 0, 0, 0).timetuple())
        
        return
    
    def get_content_rv(self):
        """ Get the content rv """
        content_ids = [1,2,3,4,5,6] # like cnn.com,bbc.com etc
        probabilities = [0.5,0.3,0.1,0.05,0.04,0.01]
        self.content_rv = stats.rv_discrete(values=(content_ids, probabilities), name='content_rv')
        return self.content_rv
    
    def get_error_code_rv(self):
        """ Get the error code rv """
        codes = [200, 206, 302, 404,500]
        probs = [0.7131, 0.0267, 0.0237, 0.019, 0.2175]
        self.error_codes_rv = stats.rv_discrete(values=(codes, probs), name='error_codes_rv')
        return self.error_codes_rv
    
    def get_country_rv(self):
        """ Get the country rv """
        [rows, country_ids, probabilities] = Database.get_country_distribution(self.db)
        self.country_rv = stats.rv_discrete(values=(country_ids, probabilities), name='country_rv')
        return self.country_rv
        
    def get_city_rv(self, country_id):
        """ Get the city rv """
        city_rv = None
        if self.country_city_rv_dict.has_key(country_id) is False:
            [rows, city_ids, probabilities] = Database.get_city_distribution(self.db, country_id)
            city_rv = stats.rv_discrete(values=(city_ids, probabilities), name='city_rv')
            self.country_city_rv_dict[country_id] = city_rv
        else:
            city_rv = self.country_city_rv_dict[country_id]
        return city_rv     
    
    def get_os_rv(self, country_id):
        """ Get the OS rv given a country """
        os_rv = None
        if self.country_os_rv_dict.has_key(int(country_id)) is False:
            [rows, os_ids, probabilities] = Database.get_os_distribution(self.db, country_id)
            os_rv = stats.rv_discrete(values=(os_ids, probabilities), name='os_rv')
            self.country_os_rv_dict[country_id] = os_rv
        else:
            os_rv = self.country_os_rv_dict[country_id]
        return os_rv
    
    def get_os_version_rv(self, os_id):
        """ Get the OS version rv given an OS """
        os_version_rv = None
        if self.os_version_rv_dict.has_key(os_id) is False:
            [rows, os_version_ids, probabilities] = Database.get_os_version_distribution(self.db, os_id)
            os_version_rv = stats.rv_discrete(values=(os_version_ids, probabilities), name='os_version_rv')
            self.os_version_rv_dict[os_id] = os_version_rv
        else:
            os_version_rv = self.os_version_rv_dict[os_id]
        return os_version_rv
    
    def get_browser_rv(self, key):
        """ Get browser rv given OS """
        rv = None
        if self.os_browser_rv_dict.has_key(key) is False:
            [rows, ids, probabilities] = Database.get_browser_distribution(self.db, key)
            rv = stats.rv_discrete(values=(ids, probabilities), name='browser_rv')
            self.os_browser_rv_dict[key] = rv
        else:
            rv = self.os_browser_rv_dict[key]
        return rv
    
    def get_browser_version_rv(self, key):
        """ Get the browser version rv """
        rv = None
        if self.browser_version_rv_dict.has_key(key) is False:
            [rows, ids, probabilities] = Database.get_browser_version_distribution(self.db, key)
            rv = stats.rv_discrete(values=(ids, probabilities), name='browser_version_rv')
            self.browser_version_rv_dict[key] = rv
        else:
            rv = self.browser_version_rv_dict[key]
        return rv
    
    def generate(self, session_id):
        
        """ Generate a single record """
        
        record = Record()
        
        # Session information. A few assumptions here: 
        #    - Session start time is randomly picked from an interval between
        #      1/1/2010 and 1/1/2011.
        #    - All sessions last between 1 second and 1 hour (60*60). 
        #    - Sessions are in one of 4 states (state_0, state_1, state_2, state_3)
        # TODO: Session state might depend on error code.
        record.session_id = session_id
        record.session_start = self.global_session_begin_time + math.floor(random.random()*31556926)
        record.session_end = record.session_start + random.randint(1, 60*60)
        record.session_state = random.randint(0, 3) 
        record.error_code = self.error_codes_rv.rvs(size=1)[0]
        
        # What country is this record from?
        country_id = self.country_rv.rvs(size=1)[0]
        record.country = self.db.id_to_country_name(country_id)
        
        # Now we have the country ID, decide the state, city, zip_code, provider and IP.
        city_rv = self.get_city_rv(country_id)
        city_id = city_rv.rvs(size=1)[0]
        [city, state, zip_code, provider, ip_address] = Database.get_city_details(self.db, city_id)
        record.state = state
        record.city = city
        record.zip = zip_code
        record.provider = provider
        record.ip_address = ip_address
        
        # Pick a random user from this country. We limit the number of users from
        # any country to 1 million.
        # TODO: Have a better scheme for user ID.
        record.user_id = random.randint((country_id-1)*1000000, (country_id*1000000)-1)
        
        # Content ID.
        record.content_id = self.content_rv.rvs(size=1)[0]
        
        # What is the OS?
        os_rv = self.get_os_rv(country_id)
        os_id = os_rv.rvs(size=1)[0]
        record.os_type = self.db.id_to_os_name(os_id)
        
        # OS version. We are assuming this distribution is dependent only on the OS id.
        os_version_rv = self.get_os_version_rv(os_id)
        os_version_id = os_version_rv.rvs(size=1)[0]
        record.os_version = self.db.id_to_os_version(os_version_id)
        
        # Browser. Assumption: This is dependent on OS only.
        browser_rv = self.get_browser_rv(os_id)
        browser_id = browser_rv.rvs(size=1)[0]
        record.browser_type = self.db.id_to_browser_name(browser_id)
        
        # Browser version. 
        browser_version_rv = self.get_browser_version_rv(browser_id)
        browser_version_id = browser_version_rv.rvs(size=1)[0]
        record.browser_version = self.db.id_to_browser_version(browser_version_id)
        
        # Flash type and version. Random stuff for now.
        # TODO: Should be dependent on the OS and browser.
        record.flash_type = "Flash " + str(random.randint(1,3))
        record.flash_version = "Version " + str(random.randint(1,5))
        
        return record