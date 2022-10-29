/**
 * @author: sucf
 * @create: 2020-10-16 11:22:12
 * @description: result返回值code对照码
 */
public enum CodeEnum {

    /**
     * 请求成功
     */
    SUCCESS("00000", "请求成功"),

    // A级别 前端用户端错误
    /**
     * 参数错误,A一级宏观错误
     */
    PARAMETER_ERROR("A0001", "参数错误"),

    /**
     * 入参异常报错,A二级宏观错误
     */
    PARAMETER_EXCEPTION_ERROR("A1000", "参数数据异常"),

    /**
     * 维度,度量不能同时为空
     */
    NOT_MEASURE_DIMENSIONS_CALCULATE("A1001", "维度,度量不能同时为空"),

    /**
     * 根据workBookId找不到库中的数据
     */
    NOT_FIND_WORKBOOK("A1002", "找不到该工作薄"),

    /**
     * 参数异常,解锁者非上锁者
     */
    UNLOCKER_NOT_LOCKER_ERROR("A1003", "参数异常,解锁者非上锁者"),

    /**
     * 无锁解锁
     */
    UNLOCK_WITHOUT_LOCK("A1004", "工作薄当前无锁"),

    /**
     * 报表保存失效
     */
    REPORT_SAVE_INVALID("A1010","报表保存当前不可编辑,需要重新进入"),

    // sql用户端
    /**
     * 入参sql端,没有找到合适function
     */
    NOT_FIND_MEASURE_HANDLE_ERROR("A1100", "没有合适的function处理器"),

    /**
     * 入参sql过滤器端,没有找到合适operator
     */
    NOT_FIND_OPERATOR_HANDLE_ERROR("A1101","没有合适的过滤Operator处理器"),

    // 埋点数据 B3+
    /**
     * 埋点日志分割字段太短
     */
    PARAM_SEPARATOR_LENGTH_SHORT("A3001", "埋点日志分割字段太短,不能少于5个"),

    // B级别 系统错误,NPE
    /**
     * 系统内部报错,B一级宏观错误
     *     后端确定不了的，选这个
     */
    SYSTEM_ERROR("B0001", "系统内部错误"),

    /**
     * 工作簿上锁异常
     */
    WORKBOOK_LOCK_ERROR("B0011","工作薄上锁失败"),

    // sql系统端 报表层面 B1+
    /**
     * 系统数据查询sql错误
     */
    SYSTEM_SQL_ERROR("B1100", "sql异常"),

    /**
     * sql执行异常
     */
    SQL_EXECUTE_ERROR("B1101", "sql执行错误"),

    /**
     * sql语法异常
     */
    SQL_SYNTAX_CREATE_TABLE_ERROR("B1111", "sql语法异常"),

    /**
     * 建表sql必须要以create开头
     */
    SQL_SYNTAX_START_CREATE_ERROR("B1112", "建表sql必须要以create开头"),

    // 数据集 B2+
    /**
     * 数据源不存在
     */
    NOT_FIND_DATASOURCE_INFO_ERROR("B2001", "数据源不存在"),

    /**
     * 数据表名已存在
     */
    EXIST_TABLE_ERROR("B2002", "数据表名已存在"),

    /**
     * 创建表失败
     */
    CREATE_TABLE_ERROR("B2003", "创建表失败"),

    /**
     * sql中依赖表在das库中不存在
     */
    BUILD_SQL_TABLE_NOT_EXIST_ERROR("B2004", "sql中依赖表在das库中不存在"),

    /**
     * 同步数据失败
     */
    SYNC_DATA_ERROR("B2008", "同步数据失败"),

    /**
     * 加速构建表失败
     */
    PROCESS_BUILD_ERROR("B2009", "加速构建失败"),

    /**
     * 来源类型不合理
     */
    SOURCE_TYPE_NOT_REASONABLE("B2015", "来源类型不合理"),


    ;
    private String code;

    private String text;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    CodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
