/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.avlist;

/**
 * @author Tom Gaskins
 * @version $Id: AVKey.java 13005 2010-01-14 00:30:41Z dcollins $
 * @comments yecol 2010.4.6
 */
public interface AVKey // TODO: Eliminate unused constants, if any
{
    // NOTE: Keep all keys in alphabetical order except where noted
	//字母序常量表，值为配置文件中的名。

    // Direction constants
    final String NORTHWEST = "gov.nasa.worldwind.layers.ViewControlsLayer.NorthWest";
    final String SOUTHWEST = "gov.nasa.worldwind.layers.ViewControlsLayer.SouthWest";
    final String NORTHEAST = "gov.nasa.worldwind.layers.ViewControlsLayer.NorthEast";
    final String SOUTHEAST = "gov.nasa.worldwind.layers.ViewControlsLayer.SouthEast";

    // Start alphabetic order
    final String ABOVE_GROUND_LEVEL = "gov.nasa.worldwind.avkey.AboveGroundLevel";
    final String ABOVE_GROUND_REFERENCE = "gov.nasa.worldwind.avkey.AboveGroundReference";
    final String ABOVE_MEAN_SEA_LEVEL = "gov.nasa.worldwind.avkey.AboveMeanSeaLevel";
    final String AIRSPACE_GEOMETRY_CACHE_SIZE = "gov.nasa.worldwind.avkey.AirspaceGeometryCacheSize";
    final String AVAILABLE_IMAGE_FORMATS = "gov.nasa.worldwind.avkey.AvailableImageFormats";
    final String AVERAGE_TILE_SIZE = "gov.nasa.worldwind.avkey.AverageTileSize";

    final String BEGIN = "gov.nasa.worldwind.avkey.Begin";
    final String BIG_ENDIAN = "gov.nasa.worldwind.avkey.BigEndian";
    final String BOTTOM = "gov.nasa.worldwind.avkey.Bottom";
    final String BYTE_ORDER = "gov.nasa.worldwind.avkey.ByteOrder";

    final String CENTER = "gov.nasa.worldwind.avkey.Center";
    final String CLOCKWISE = "gov.nasa.worldwind.avkey.ClockWise";
    final String CLOSE = "gov.nasa.worldwind.avkey.Close";
    final String CONSTRUCTION_PARAMETERS = "gov.nasa.worldwind.avkey.ConstructionParameters";
    final String COUNTER_CLOCKWISE = "gov.nasa.worldwind.avkey.CounterClockWise";

    final String DATA_CACHE_NAME = "gov.nasa.worldwind.avkey.DataCacheNameKey";
    final String DATA_FILE_STORE_CLASS_NAME = "gov.nasa.worldwind.avkey.DataFileStoreClassName";
    final String DATA_FILE_STORE_CONFIGURATION_FILE_NAME =
        "gov.nasa.worldwind.avkey.DataFileStoreConfigurationFileName";
    final String DATA_TYPE = "gov.nasa.worldwind.avkey.DataType";
    final String DATASET_NAME = "gov.nasa.worldwind.avkey.DatasetNameKey";
    final String DATASET_TYPE = "gov.nasa.worldwind.avkey.DatasetTypeKey";
    final String DELETE_CACHE_ON_EXIT = "gov.nasa.worldwind.avkey.DeleteCacheOnExit";
    final String DESCRIPTION = "gov.nasa.worldwind.avkey.Description";
    final String DETAIL_HINT = "gov.nasa.worldwind.avkey.DetailHint";
    final String DISPLAY_NAME = "gov.nasa.worldwind.avkey.DisplayName";
    final String DISPLAY_ICON = "gov.nasa.worldwind.avkey.DisplayIcon";

    final String EARTH_ELEVATION_MODEL_CAPABILITIES = "gov.nasa.worldwind.avkey.EarthElevationModelCapabilities";
    final String EARTH_ELEVATION_MODEL_CLASS_NAME = "gov.nasa.worldwind.avkey.EarthElevationModelClassName";
    final String EARTH_ELEVATION_MODEL_CONFIG_FILE = "gov.nasa.worldwind.avkey.EarthElevationModelConfigFile";
    final String EAST = "gov.nasa.worldwind.avkey.East";

    final String ELEVATION = "gov.nasa.worldwind.avkey.Elevation";
    final String ELEVATION_EXTREMES_FILE = "gov.nasa.worldwind.avkey.ElevationExtremesFileKey";
    final String ELEVATION_EXTREMES_LOOKUP_CACHE_SIZE = "gov.nasa.worldwind.avkey.ElevationExtremesLookupCacheSize";
    final String ELEVATION_MIN = "gov.nasa.worldwind.avkey.ElevationMinKey";
    final String ELEVATION_MAX = "gov.nasa.worldwind.avkey.ElevationMaxKey";
    final String ELEVATION_MODEL = "gov.nasa.worldwind.avkey.ElevationModel";
    final String ELEVATION_MODEL_FACTORY = "gov.nasa.worldwind.avkey.ElevationModelFactory";
    final String ELEVATION_TILE_CACHE_SIZE = "gov.nasa.worldwind.avkey.ElevationTileCacheSize";
    final String END = "gov.nasa.worldwind.avkey.End";

    final String EXPIRY_TIME = "gov.nasa.worldwind.avkey.ExpiryTime";
    final String EXTENT = "gov.nasa.worldwind.avkey.Extent";
    final String EXTERNAL_LINK = "gov.nasa.worldwind.avkey.ExternalLink";

    final String FEEDBACK_ENABLED = "gov.nasa.worldwind.avkey.FeedbackEnabled";
    final String FEEDBACK_REFERENCE_POINT = "gov.nasa.worldwind.avkey.FeedbackReferencePoint";    
    final String FEEDBACK_SCREEN_BOUNDS = "gov.nasa.worldwind.avkey.FeedbackScreenBounds";
    final String FILE_SIZE = "gov.nasa.worldwind.avkey.FileSize";
    final String FILE_STORE = "gov.nasa.worldwind.avkey.FileStore";
    final String FILE_STORE_LOCATION = "gov.nasa.worldwind.avkey.FileStoreLocation";
    final String FLOAT32 = "gov.nasa.worldwind.avkey.Float32";
    final String FORMAT_SUFFIX = "gov.nasa.worldwind.avkey.FormatSuffixKey";
    final String FOV = "gov.nasa.worldwind.avkey.FieldOfView";
    final String FORCE_LEVEL_ZERO_LOADS = "gov.nasa.worldwind.avkey.ForceLevelZeroLoads";
    final String RETAIN_LEVEL_ZERO_TILES = "gov.nasa.worldwind.avkey.RetainLevelZeroTiles";

    final String GET_CAPABILITIES_URL = "gov.nasa.worldwind.avkey.GetCapabilitiesURL";
    final String GET_MAP_URL = "gov.nasa.worldwind.avkey.GetMapURL";
    final String GLOBE = "gov.nasa.worldwind.avkey.GlobeObject";
    final String GLOBE_CLASS_NAME = "gov.nasa.worldwind.avkey.GlobeClassName";
    final String GREAT_CIRCLE = "gov.nasa.worldwind.avkey.GreatCircle";

    final String HEIGHT = "gov.nasa.worldwind.avkey.Height";
    final String HORIZONTAL = "gov.nasa.worldwind.avkey.Horizontal";
    final String HOVER_TEXT = "gov.nasa.worldwind.avkey.HoverText";

    final String ICON_NAME = "gov.nasa.worldwind.avkey.IconName";
    final String IMAGE = "gov.nasa.worldwind.avkey.Image";
    final String IMAGE_FORMAT = "gov.nasa.worldwind.avkey.ImageFormat";
    final String INACTIVE_LEVELS = "gov.nasa.worldwind.avkey.InactiveLevels";
    final String INSTALLED = "gov.nasa.worldwind.avkey.Installed";
    final String INITIAL_ALTITUDE = "gov.nasa.worldwind.avkey.InitialAltitude";
    final String INITIAL_HEADING = "gov.nasa.worldwind.avkey.InitialHeading";
    final String INITIAL_LATITUDE = "gov.nasa.worldwind.avkey.InitialLatitude";
    final String INITIAL_LONGITUDE = "gov.nasa.worldwind.avkey.InitialLongitude";
    final String INITIAL_PITCH = "gov.nasa.worldwind.avkey.InitialPitch";
    final String INPUT_HANDLER_CLASS_NAME = "gov.nasa.worldwind.avkey.InputHandlerClassName";
    final String INT8 = "gov.nasa.worldwind.avkey.Int8";
    final String INT16 = "gov.nasa.worldwind.avkey.Int16";
    final String INT32 = "gov.nasa.worldwind.avkey.Int32";

    final String LAST_UPDATE = "gov.nasa.worldwind.avkey.LastUpdateKey";
    final String LAYER = "gov.nasa.worldwind.avkey.LayerObject";
    final String LAYER_FACTORY = "gov.nasa.worldwind.avkey.LayerFactory";
    final String LAYER_NAMES = "gov.nasa.worldwind.avkey.LayerNames";
    final String LAYERS = "gov.nasa.worldwind.avkey.LayersObject";
    final String LAYERS_CLASS_NAMES = "gov.nasa.worldwind.avkey.LayerClassNames";
    final String LEFT = "gov.nasa.worldwind.avkey.Left";
    final String LEVEL_NAME = "gov.nasa.worldwind.avkey.LevelNameKey";
    final String LEVEL_NUMBER = "gov.nasa.worldwind.avkey.LevelNumberKey";
    final String LEVEL_ZERO_TILE_DELTA = "gov.nasa.worldwind.avkey.LevelZeroTileDelta";
    final String LINEAR = "gov.nasa.worldwind.avkey.Linear";
    final String LITTLE_ENDIAN = "gov.nasa.worldwind.avkey.LittleEndian";
    final String LOGGER_NAME = "gov.nasa.worldwind.avkey.LoggerName";
    final String LOXODROME = "gov.nasa.worldwind.avkey.Loxodrome";
    
    final String MAP_SCALE = "gov.nasa.worldwind.avkey.MapScale";
    final String MARS_ELEVATION_MODEL_CLASS_NAME = "gov.nasa.worldwind.avkey.MarsElevationModelClassName";
    final String MARS_ELEVATION_MODEL_CONFIG_FILE = "gov.nasa.worldwind.avkey.MarsElevationModelConfigFile";
    final String MARS_MOCCOLOR_ONE_IMAGE_PATH = "gov.nasa.worldwind.avkey.MarsMOCColorOneImagePath";
    final String MAX_ABSENT_TILE_ATTEMPTS = "gov.nasa.worldwind.avkey.MaxAbsentTileAttempts";
    final String MAX_ACTIVE_ALTITUDE = "gov.nasa.worldwind.avkey.MaxActiveAltitude";
    final String MEMORY_CACHE_SET_CLASS_NAME = "gov.nasa.worldwind.avkey.MemoryCacheSetClassName";
    final String MIME_TYPE = "gov.nasa.worldwind.avkey.MimeType";
    final String MIN_ABSENT_TILE_CHECK_INTERVAL = "gov.nasa.worldwind.avkey.MinAbsentTileCheckInterval";
    final String MIN_ACTIVE_ALTITUDE = "gov.nasa.worldwind.avkey.MinActiveAltitude";

    // Implementation note: the keys MISSING_DATA_SIGNAL and MISSING_DATA_REPLACEMENT are intentionally different than
    // their actual string values. Legacy code is expecting the string values "MissingDataFlag" and "MissingDataValue",
    // respectively.
    final String MISSING_DATA_SIGNAL = "gov.nasa.worldwind.avkey.MissingDataFlag";
    final String MISSING_DATA_REPLACEMENT = "gov.nasa.worldwind.avkey.MissingDataValue";

    final String MODEL = "gov.nasa.worldwind.avkey.ModelObject";
    final String MODEL_CLASS_NAME = "gov.nasa.worldwind.avkey.ModelClassName";
    final String MOON_CLEMENTIN40_ONE_IMAGE_PATH = "gov.nasa.worldwind.avkey.MoonClementine40OneImagePath";
    final String MOON_ELEVATION_MODEL_CLASS_NAME = "gov.nasa.worldwind.avkey.MoonElevationModelClassName";
    final String MOON_ELEVATION_MODEL_CONFIG_FILE = "gov.nasa.worldwind.avkey.MoonElevationModelConfigFile";

    final String NETWORK_STATUS_CLASS_NAME = "gov.nasa.worldwind.avkey.NetworkStatusClassName";
    final String NEXT = "gov.nasa.worldwind.avkey.Next";
    final String NUM_EMPTY_LEVELS = "gov.nasa.worldwind.avkey.NumEmptyLevels";
    final String NUM_LEVELS = "gov.nasa.worldwind.avkey.NumLevels";
    final String NETWORK_RETRIEVAL_ENABLED = "gov.nasa.worldwind.avkey.NetworkRetrievalEnabled";
    final String NORTH = "gov.nasa.worldwind.avkey.North";

    final String OFFLINE_MODE = "gov.nasa.worldwind.avkey.OfflineMode";
    final String OPACITY = "gov.nasa.worldwind.avkey.Opacity";

    final String PAUSE = "gov.nasa.worldwind.avkey.Pause";
    final String PICKED_OBJECT = "gov.nasa.worldwind.avkey.PickedObject";
    final String PICKED_OBJECT_ID = "gov.nasa.worldwind.avkey.PickedObject.ID";
    final String PICKED_OBJECT_PARENT_LAYER = "gov.nasa.worldwind.avkey.PickedObject.ParentLayer";
    final String PICKED_OBJECT_PARENT_LAYER_NAME = "gov.nasa.worldwind.avkey.PickedObject.ParentLayer.Name";
    final String PICKED_OBJECT_SIZE = "gov.nasa.worldwind.avkey.PickedObject.Size";
    final String PIXEL_FORMAT = "gov.nasa.worldwind.avkey.PixelFormat";
    final String PIXEL_TYPE = "gov.nasa.worldwind.avkey.PixelType";
    final String PLACENAME_LAYER_CACHE_SIZE = "gov.nasa.worldwind.avkey.PlacenameLayerCacheSize";
    final String PLAY = "gov.nasa.worldwind.avkey.Play";
    final String POSITION = "gov.nasa.worldwind.avkey.Position";
    final String PREVIOUS = "gov.nasa.worldwind.avkey.Previous";
    final String PROGRESS = "gov.nasa.worldwind.avkey.Progress";
    final String PROGRESS_MESSAGE = "gov.nasa.worldwind.avkey.ProgressMessage";

    final String RECTANGULAR_TESSELLATOR_MAX_LEVEL = "gov.nasa.worldwind.avkey.RectangularTessellatorMaxLevel";
    final String RESIZE = "gov.nasa.worldwind.avkey.Resize";
    /**
     * On window resize, scales the item to occupy a constant relative size of the viewport.
     */
    final String RESIZE_STRETCH = "gov.nasa.worldwind.CompassLayer.ResizeStretch";
    /**
     * On window resize, scales the item to occupy a constant relative size of the viewport, but not larger than
     * the item's inherent size scaled by the layer's item scale factor.
     */
    final String RESIZE_SHRINK_ONLY = "gov.nasa.worldwind.CompassLayer.ResizeShrinkOnly";
    /**
     * Does not modify the item size when the window changes size.
     */
    final String RESIZE_KEEP_FIXED_SIZE = "gov.nasa.worldwind.CompassLayer.ResizeKeepFixedSize";
    final String RETRIEVAL_POOL_SIZE = "gov.nasa.worldwind.avkey.RetrievalPoolSize";
    final String RETRIEVAL_QUEUE_SIZE = "gov.nasa.worldwind.avkey.RetrievalQueueSize";
    final String RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT = "gov.nasa.worldwind.avkey.RetrievalStaleRequestLimit";
    final String RETRIEVAL_SERVICE_CLASS_NAME = "gov.nasa.worldwind.avkey.RetrievalServiceClassName";
    final String RETRIEVER_STATE = "gov.nasa.worldwind.avkey.RetrieverState";
    final String RETRIEVE_PROPERTIES_FROM_SERVICE = "gov.nasa.worldwind.avkey.RetrievePropertiesFromService";
    final String RHUMB_LINE = "gov.nasa.worldwind.avkey.RhumbLine";
    final String RIGHT = "gov.nasa.worldwind.avkey.Right";
    final String ROLLOVER_TEXT = "gov.nasa.worldwind.avkey.RolloverText";

    final String SCENE_CONTROLLER = "gov.nasa.worldwind.avkey.SceneControllerObject";
    final String SCENE_CONTROLLER_CLASS_NAME = "gov.nasa.worldwind.avkey.SceneControllerClassName";
    final String SCREEN_CREDIT = "gov.nasa.worldwind.avkey.ScreenCredit";
    final String SCREEN_CREDIT_LINK = "gov.nasa.worldwind.avkey.ScreenCreditLink";
    final String SECTOR = "gov.nasa.worldwind.avKey.Sector";
    final String SECTOR_GEOMETRY_CACHE_SIZE = "gov.nasa.worldwind.avkey.SectorGeometryCacheSize";
    final String SECTOR_RESOLUTION_LIMITS = "gov.nasa.worldwind.avkey.SectorResolutionLimits";
    final String SECTOR_RESOLUTION_LIMIT = "gov.nasa.worldwind.avkey.SectorResolutionLimit";
    final String SENDER = "gov.nasa.worldwind.avkey.Sender";
    final String SERVICE = "gov.nasa.worldwind.avkey.ServiceURLKey";
    final String SERVICE_NAME = "gov.nasa.worldwind.avkey.ServiceName";
    final String SESSION_CACHE_CLASS_NAME = "gov.nasa.worldwind.avkey.SessionCacheClassName";
    final String SHAPE_ATTRIBUTES = "gov.nasa.worldwind.avkey.ShapeAttributes";
    final String SHAPE_LINE = "gov.nasa.worldwind.avkey.ShapeLine";
    final String SHAPE_PATH = "gov.nasa.worldwind.avkey.ShapePath";
    final String SHAPE_POLYGON = "gov.nasa.worldwind.avkey.ShapePolygon";
    final String SHAPE_CIRCLE = "gov.nasa.worldwind.avkey.ShapeCircle";
    final String SHAPE_ELLIPSE = "gov.nasa.worldwind.avkey.ShapeEllipse";
    final String SHAPE_QUAD = "gov.nasa.worldwind.avkey.ShapeQuad";
    final String SHAPE_SQUARE = "gov.nasa.worldwind.avkey.ShapeSquare";
    final String SPLIT_SCALE = "gov.nasa.worldwind.avkey.SplitScale";
    final String SOUTH = "gov.nasa.worldwdind.avkey.South";
    final String START = "gov.nasa.worldwind.avkey.Start";
    final String STOP = "gov.nasa.worldwind.avkey.Stop";
    final String STYLE_NAMES = "gov.nasa.worldwind.avkey.StyleNames";

    final String TESSELLATOR_CLASS_NAME = "gov.nasa.worldwind.avkey.TessellatorClassName";
    final String TEXTURE_CACHE_SIZE = "gov.nasa.worldwind.avkey.TextureCacheSize";
    final String TEXTURE_COORDINATES = "gov.nasa.worldwind.avkey.TextureCoordinates";
    final String TEXTURE_IMAGE_CACHE_SIZE = "gov.nasa.worldwind.avkey.TextureTileCacheSize";
    final String TASK_POOL_SIZE = "gov.nasa.worldwind.avkey.TaskPoolSize";
    final String TASK_QUEUE_SIZE = "gov.nasa.worldwind.avkey.TaskQueueSize";
    final String TASK_SERVICE_CLASS_NAME = "gov.nasa.worldwind.avkey.TaskServiceClassName";
    final String TEXT = "gov.nasa.worldwind.avkey.Text";
    final String TEXT_EFFECT_NONE = "gov.nasa.worldwind.avkey.TextEffectNone";
    final String TEXT_EFFECT_OUTLINE = "gov.nasa.worldwind.avkey.TextEffectOutline";
    final String TEXT_EFFECT_SHADOW = "gov.nasa.worldwind.avkey.TextEffectShadow";
    final String TILE_DELTA = "gov.nasa.worldwind.avkey.TileDeltaKey";
    final String TILE_HEIGHT = "gov.nasa.worldwind.avkey.TileHeightKey";
    final String TILE_ORIGIN = "gov.nasa.worldwind.avkey.TileOrigin";
    final String TILE_RETRIEVER = "gov.nasa.worldwind.avkey.TileRetriever";
    final String TILE_URL_BUILDER = "gov.nasa.worldwind.avkey.TileURLBuilder";
    final String TILE_WIDTH = "gov.nasa.worldwind.avkey.TileWidthKey";
    final String TILED_IMAGERY = "gov.nasa.worldwind.avkey.TiledImagery";
    final String TILED_ELEVATIONS = "gov.nasa.worldwind.avkey.TiledElevations";
    final String TILED_RASTER_PRODUCER_CACHE_SIZE = "gov.nasa.worldwind.avkey.TiledRasterProducerCacheSize";
    final String TILED_RASTER_PRODUCER_LARGE_DATASET_THRESHOLD =
        "gov.nasa.worldwind.avkey.TiledRasterProducerLargeDatasetThreshold";
    final String TITLE = "gov.nasa.worldwind.avkey.Title";
    final String TOP = "gov.nasa.worldwind.avkey.Top";
    final String TRANSPARENCY_COLORS = "gov.nasa.worldwind.avkey.TransparencyColors";

    final String URL = "gov.nasa.worldwind.avkey.URL";
    final String URL_CONNECT_TIMEOUT = "gov.nasa.worldwind.avkey.URLConnectTimeout";
    final String URL_PROXY_HOST = "gov.nasa.worldwind.avkey.UrlProxyHost";
    final String URL_PROXY_PORT = "gov.nasa.worldwind.avkey.UrlProxyPort";
    final String URL_PROXY_TYPE = "gov.nasa.worldwind.avkey.UrlProxyType";
    final String URL_READ_TIMEOUT = "gov.nasa.worldwind.avkey.URLReadTimeout";
    final String USE_MIP_MAPS = "gov.nasa.worldwind.avkey.UseMipMaps";
    final String USE_TRANSPARENT_TEXTURES = "gov.nasa.worldwind.avkey.UseTransparentTextures";

    final String VERSION = "gov.nasa.worldwind.avkey.Version";
    final String VERTICAL = "gov.nasa.worldwind.avkey.Vertical";
    final String VERTICAL_EXAGGERATION = "gov.nasa.worldwind.avkey.VerticalExaggeration";
    final String VERTICAL_EXAGGERATION_UP = "gov.nasa.worldwind.avkey.VerticalExaggerationUp";
    final String VERTICAL_EXAGGERATION_DOWN = "gov.nasa.worldwind.avkey.VerticalExaggerationDown";
    final String VIEW = "gov.nasa.worldwind.avkey.ViewObject";
    final String VIEW_CLASS_NAME = "gov.nasa.worldwind.avkey.ViewClassName";
    final String VIEW_INPUT_HANDLER_CLASS_NAME = "gov.nasa.worldwind.avkey.ViewInputHandlerClassName";
    final String VIEW_QUIET = "gov.nasa.worldwind.avkey.ViewQuiet";

    // Viewing operations
    final String VIEW_OPERATION = "gov.nasa.worldwind.avkey.ViewOperation";
    final String VIEW_PAN = "gov.nasa.worldwind.avkey.Pan";
    final String VIEW_LOOK = "gov.nasa.worldwind.avkey.ControlLook";
    final String VIEW_HEADING_LEFT = "gov.nasa.worldwind.avkey.HeadingLeft";
    final String VIEW_HEADING_RIGHT = "gov.nasa.worldwind.avkey.HeadingRight";
    final String VIEW_ZOOM_IN = "gov.nasa.worldwind.avkey.ZoomIn";
    final String VIEW_ZOOM_OUT = "gov.nasa.worldwind.avkey.ZoomOut";
    final String VIEW_PITCH_UP = "gov.nasa.worldwind.avkey.PitchUp";
    final String VIEW_PITCH_DOWN = "gov.nasa.worldwind.avkey.PitchDown";
    final String VIEW_FOV_NARROW = "gov.nasa.worldwind.avkey.FovNarrow";
    final String VIEW_FOV_WIDE = "gov.nasa.worldwind.avkey.FovWide";

    final String WEST = "gov.nasa.worldwind.avkey.West";
    final String WIDTH = "gov.nasa.worldwind.avkey.Width";
    final String WMS_BACKGROUND_COLOR = "gov.nasa.worldwind.avkey.BackgroundColor";
    final String WMS_VERSION = "gov.nasa.worldwind.avkey.WMSVersion";
    final String WORLD_WINDOW_CLASS_NAME = "gov.nasa.worldwind.avkey.WorldWindowClassName";
    final String WORLD_MAP_IMAGE_PATH = "gov.nasa.worldwind.avkey.WorldMapImagePath";
    final String WORLD_WIND_DOT_NET_LAYER_SET = "gov.nasa.worldwind.avkey.WorldWindDotNetLayerSet";
    final String WORLD_WIND_DOT_NET_PERMANENT_DIRECTORY = "gov.nasa.worldwind.avkey.WorldWindDotNetPermanentDirectory";
}
