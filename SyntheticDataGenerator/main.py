import argparse, logging, random, sqlite3
from database import *
from record import *
from generator import *
from scipy import stats

def main():
    
    """ Main """
    
    #
    # Setup the logger.
    #
    logger = logging.getLogger()
    ch = logging.StreamHandler()
    formatter = logging.Formatter("%(asctime)s [%(module)s] [%(levelname)s] %(message)s")
    ch.setFormatter(formatter)
    logger.addHandler(ch)
    
    #
    # Parse the arguments
    #
    parser = argparse.ArgumentParser(description="Generates synthetic data.")
    parser.add_argument('-n', dest='record_count', action='store', type=int,
                        default='100', help='number of records to generate (default=%(default)s)')
    parser.add_argument('-s', dest='start_session_id', action='store', type=int,
                        default='1', help='the starting session id (default=%(default)s)')
    parser.add_argument('-v', action="store_true", default=False, help='verbose mode')
    args = parser.parse_args()
    if args.record_count < 1:
        logger.error('Invalid record count!')
        exit()
    
    if args.v:
        logger.setLevel(logging.INFO)
    
    # Initialize database. 
    db = Database()
    
    # Initialize the generator.
    generator = Generator(db)
    
    # Generate the record.
    for idx in range(0, args.record_count):
        session_id = args.start_session_id + idx
        record = generator.generate(session_id)
        #if (idx % (int(args.record_count/10)) == 0):
        if (idx % 10000) == 0:
            logger.info("Generated %d records."%(idx))
        print record.to_string()
    
    logger.info("Complete!")
    
if __name__ == '__main__':
    main()
