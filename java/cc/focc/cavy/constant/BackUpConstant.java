package cc.focc.cavy.constant;

public class BackUpConstant {

    public static final String BACKUP_FILE_PREFIX = "/data";

    public static final String BACKUP_FILE_MYSQL_SUFFIX = ".sql";
    public static final String BACKUP_FILE_MONGO_SUFFIX = ".bson";
    public static final String BACKUP_FILE_ZIP_SUFFIX = ".zip";

    public static final String CMD_MYSQL_ALL_DATABASE = "mysql -h%s -u%s -P%s -p'%s' -e \"show databases\" | grep -Ev \"Database\"";
    public static final String CMD_MYSQL_ALL_TABLE = "mysql -h%s -u%s -P%s -p'%s' -e  \"use %s;show tables\" | grep -Ev \"Tables_in_%s\"";
    public static final String CMD_MYSQL_CREATE_DATABASE = "mysql -h%s -u%s -P%s -p'%s' -e \"create database if not exists %s\"";
    public static final String CMD_MYSQL_DUMP_TABLE = "mysqldump -h%s -u%s -P%s -p'%s' --single-transaction  -R --default-character-set=utf8  --skip-lock-tables --force --quick --skip-triggers --skip-dump-date --set-gtid-purged=off --max_allowed_packet=1024M --skip-tz-utc %s %s > %s";
    public static final String CMD_MYSQL_RECOVER = "mysql -h%s -u%s  -P%s  -p'%s' %s --force < %s ";

    public static final String CMD_CREATE_DIR = "mkdir -p %s";

    public static final String CMD_ZIP_COMPRESS = "zip -j -9r -P %s %s %s";
    public static final String CMD_ZIP_UNPACK = "unzip -o -P %s %s %s";


}
