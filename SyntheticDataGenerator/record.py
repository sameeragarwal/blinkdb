class Record:
    
    """ This class defines a record """
    
    session_id = None
    session_start = None
    session_end = None
    session_state = None
    error_code = None
    
    user_id = None
    content_id = None
    ip_address = None
    provider = None
    browser_type = None
    browser_version = None
    os_type = None
    os_version = None
    flash_type = None
    flash_version = None
    country = None
    state = None
    city = None
    zip_code = None
    
    def __init__(self):
        return
    
    def next(self):
        """ Generate a random record. Note that we need the country id to be set. """ 
        return
    
    def to_string(self):
        """ Convert the record to string """
        record_string = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n" % (
                    self.session_id, self.session_start, self.session_end, 
                    self.session_state, self.error_code, self.user_id, 
                    self.content_id, self.ip_address, self.provider,
                    self.browser_type, self.browser_version, self.os_type, 
                    self.os_version, self.flash_type, self.flash_version, 
                    self.country, self.state, self.city, self.zip_code)
        return record_string
        