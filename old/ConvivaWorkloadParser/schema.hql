ADD FILE dist/hive/ConvivaSerDe.jar;
ADD FILE dist/hive/ConvivaUDF.jar;

DROP TABLE anon_sdm2_ss;
CREATE TABLE anon_sdm2_ss ( 

    startTimeMs BIGINT,
    customerId STRING,
    clientId STRING,
    clientSessionId INT,
    clientIp STRING,

    latitude FLOAT,
    longitude FLOAT,
    continent TINYINT,
    country SMALLINT,
    countryCf TINYINT,

    state SMALLINT,
    stateCf TINYINT,
    city INT,
    cityCf TINYINT,
    asn INT,

    connType TINYINT,
    dma INT,
    postalCode STRING,
    errorFlags SMALLINT,
    isp INT,

    tags STRING,
    objectId STRING,
    joinTimeMs INT,
    endedFlag BOOLEAN,

    lastPlayerState STRING,
    lastSessionState STRING,
    justJoined BOOLEAN,
    lifeAverageBitrateKbps INT,
    lastServerIp STRING,

    fSessionTimeMs INT,
    lSessionTimeMs INT,
    fSeqNum INT,
    lSeqNum INT,
    firstHbTimeMs BIGINT,

    lastHbTimeMs BIGINT,
    fResourceState STRING,
    lResourceState STRING,
    fResourceId INT,
    lResourceId INT,

    fBitrateKbps INT,
    lBitrateKbps INT,
    fBufferLengthMs INT,
    lBufferLengthMs INT,
    fLifeBufferingTimeMs INT,

    lLifeBufferingTimeMs INT,
    fLifePlayingTimeMs INT,
    lLifePlayingTimeMs INT,
    fLifeNumSwitches INT,
    lLifeNumSwitches INT,

    fLifeNumQualitySwitches INT,
    lLifeNumQualitySwitches INT,
    fLifeStoppedTimeMs INT,
    lLifeStoppedTimeMs INT,
    joinBitrateKbps INT,

    shareId INT,
    startBitrateKbps INT,
    startResourceState STRING,
    numEvents INT,
    firstHbSeq INT,

    lastHbSeq INT,
    contentLenSec INT,
    minEstBwKbps INT,
    maxEstBwKbps INT,
    estBwSum INT,

    estBwCount INT,
    estBwKbpsBeforeJoinSum INT,
    estBwKbpsBeforeJoinCount INT,
    stoppedTimeAtJoinMs INT,
    pausedTimeAtJoinMs INT,

    joinBuffTimeMs INT,
    firstSelectResponseTimeMs INT,
    beforeHbTimeDiffMs INT,
    afterHbTimeDiffMs INT,
    maxEncodedFps INT,

    averageFpsSum INT,
    numAverageFpsSamples INT,
    numDroppedFrames INT,
    bufferLengthMsDiff INT,
    lastBufferLengthMs INT,

    minBufferLengthMs INT,
    maxBufferLengthMs INT,
    bufferLengthMsSum INT,
    numBufferLengthMsCount INT,
    resources ARRAY,

    bitrateUsages ARRAY,
    resourceServerIpInfos ARRAY,
    joinResourceInfos ARRAY,
    lifetimeUsedResources ARRAY,

    sessionType STRING, 
    playTimeMs INT,
    sessionTimeMs INT,
    buffTimeMs INT,
    stoppedTimeMs INT,

    buffLengthMs INT,
    estBwSqrSum DOUBLE,
    justStarted BOOLEAN,
    sleepTimeMs INT,
    pausedTimeMs INT,

    fLifeSleepTimeMs INT,
    lLifeSleepTimeMs INT,
    fLifePausedTimeMs INT,
    lLifePausedTimeMs INT,
    swcVersion STRING
)

PARTITIONED BY (dt STRING)
STORED AS SEQUENCEFILE
;


