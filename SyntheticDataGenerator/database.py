import sqlite3, logging, socket, struct, random

class Database():
    
    """ Manage the SQLite database """  
    
    connection = None
    cursor = None
    logger = None
    
    # These are a few hash maps for easy access rather than going to the DB
    # again and again.
    country_city_rv_dict = {}       # city distribution in each country.
    country_os_rv_dict = {}         # os distribution in each country, this might be a strong assumption.
    
    def __init__(self):
        
        self.logger = logging.getLogger()
        try:
            self.connection = sqlite3.connect('database.db') 
            self.connection.text_factory = str 
            self.connection.row_factory = sqlite3.Row
            self.cursor = self.connection.cursor()  
        except:
            self.logger.error("Could not connect to the database.")
            exit()
        
        self.country_dict = self.load_country_dict()    
        self.os_dict = self.load_os_dictionary()
        self.os_version_dict = self.load_os_version_dictionary()
        self.browser_dict = self.load_browser_dictionary()
        self.browser_version_dict = self.load_browser_version_dictionary()
        
        return
    
    def id_to_country_name(self, key):
        return self.country_dict[key]
    
    def load_country_dict(self):
        country_dict = {}
        rows = self.cursor.execute("SELECT * FROM country")
        for row in rows:
            country_id = int(row[0])
            country_name = row[1]
            country_dict[country_id] = country_name
        return country_dict 
    
    def id_to_os_name(self, key):
        return self.os_dict[key]
        
    def load_os_dictionary(self):
        """ Return the os id to name dictionary """
        name_dict = {}
        rows = self.cursor.execute("SELECT * FROM os")
        for row in rows:
            os_id = int(row["id"])
            os_name = row["name"]
            name_dict[os_id] = os_name
        return name_dict
    
    def id_to_os_version(self, key):
        return self.os_version_dict[key]
        
    def load_os_version_dictionary(self):
        """ Return the OS version id to name dictionary """
        version_dict = {}
        rows = self.cursor.execute("SELECT * FROM os_version")
        for row in rows:
            version_id = int(row["id"])
            version_name = row["name"]
            version_dict[version_id] = version_name
        return version_dict
    
    def id_to_browser_name(self, key):
        return self.browser_dict[key]
        
    def load_browser_dictionary(self):
        """ Return the browser id to name dictionary """
        name_dict = {}
        rows = self.cursor.execute("SELECT * FROM browser")
        for row in rows:
            browser_id = int(row["id"])
            browser_name = row["name"]
            name_dict[browser_id] = browser_name
        return name_dict 

    def id_to_browser_version(self, key):
        return self.browser_version_dict[key]
        
    def load_browser_version_dictionary(self):
        """ Return the id to version name dictionary """
        version_dict = {}
        rows = self.cursor.execute("SELECT * FROM browser_version")
        for row in rows:
            version_id = int(row["id"])
            version_name = row["name"]
            version_dict[version_id] = version_name
        return version_dict
                    
    @staticmethod
    def get_country_distribution(self):
        """ Get the country distribution from the database """
        countries = []
        probabilities = []
        rows = self.cursor.execute("SELECT * FROM country ORDER BY id")
        for row in rows:
            countries.append(int(row['id']))
            probabilities.append(float(row['probability']))
    
        return [rows, countries, probabilities]      
    
    @staticmethod
    def get_city_distribution(self, country_id):
        """ Get the city distribution from the database for a given country """
        city_ids = []
        probabilities = []
        
        sql = "SELECT * FROM [location] WHERE [country_id] = %d"%(country_id)
        rows = self.cursor.execute(sql)
        for row in rows:
            city_ids.append(int(row['id']))
            probabilities.append(float(row['probability']))
        
        return [rows, city_ids, probabilities]    

    @staticmethod
    def get_city_details(self, location_id):
        """
        Find the details for the picked city. 
        TODO: Find the IP address chunk for the city and generate one from that. 
              Use MaxMind's GeoCityLite for this.
        """
        sql = "SELECT * FROM [location] WHERE [id] = %d"%(location_id)
        self.cursor.execute(sql)
        row = self.cursor.fetchone()
        city = row['city']
        state = row['region']
        zip_code = row['postal_code']
        provider = row['provider']
        ip_address_int = random.randint(3221225729, 3758096126) # Class C
        ip_address = socket.inet_ntoa(struct.pack('L', socket.htonl(ip_address_int)))
        
        return [city, state, zip_code, provider, ip_address]

    @staticmethod
    def get_os_distribution(self, country_id):
        """ Distribution of OS of the visitors given a country. """
        os_ids = []
        probabilities = []
        sql = "SELECT * FROM [os_by_country] WHERE [country_id]=%d"%(country_id)
        rows = self.cursor.execute(sql)
        for row in rows:
            os_ids.append(int(row["os_id"]))
            probabilities.append(float(row["probability"]))
        return [rows, os_ids, probabilities]
    
    @staticmethod
    def get_os_version_distribution(self, os_id):
        """ Distribution of OS version of a given OS. """
        os_version_ids = []
        probabilities = []
        sql = "SELECT * FROM [os_version] WHERE [os_id]=%d"%(os_id)
        rows = self.cursor.execute(sql)
        for row in rows:
            os_version_ids.append(int(row["id"]))
            probabilities.append(float(row["probability"]))
        return [rows, os_version_ids, probabilities]

    @staticmethod
    def get_browser_distribution(self, os_id):
        """ Distribution of browser of the visitors given an OS. """
        ids = []
        probabilities = []
        sql = "SELECT * FROM [browser_by_os] WHERE [os_id]=%d"%(os_id)
        rows = self.cursor.execute(sql)
        for row in rows:
            ids.append(int(row["browser_id"]))
            probabilities.append(float(row["probability"]))
        return [rows, ids, probabilities]
    
    @staticmethod
    def get_browser_version_distribution(self, browser_id):
        """ Distribution of browser versions given a browser. """
        ids = []
        probabilities = []
        sql = "SELECT * FROM [browser_version] WHERE [browser_id]=%d"%(browser_id)
        rows = self.cursor.execute(sql)
        for row in rows:
            ids.append(int(row["id"]))
            probabilities.append(float(row["probability"]))
        return [rows, ids, probabilities]
        