import request from '../utils/request';
export var BASE_URL=''; //接口地址
export var SW_BASE_URL=''; //接口地址
switch (import.meta.env.VITE_PROJECT_ENV) {
    case "development":
        BASE_URL = "http://127.0.0.1:9090/api/v1"; //开发环境url
        SW_BASE_URL = "ws://127.0.0.1:9090/socket/v1" //开发环境url
        break;
    case "production":
        BASE_URL = "http://127.0.0.1:9090/api/v1"; //生产环境url
        SW_BASE_URL = "ws://127.0.0.1:9090/socket/v1"
        break;
}
export const Login = data => {
    return request({
        url: BASE_URL + '/auth/login',
        method: 'post',
        data:data
    });
};

export const SendCode = () => {
    return request({
        url: BASE_URL + '/auth/sendCode',
        method: 'get'
    });
};

export const CheckToken = token => {
    return request({
        url: BASE_URL + '/auth/checkToken',
        method: 'get',
        headers: {
            'Authorization':localStorage.getItem("token")
        }
    });
};



export const CopyPwd = pwd => {
    return request({
        url: BASE_URL + '/record/view/' + pwd,
        method: 'get',
        headers: {
            'Authorization':localStorage.getItem("token")
        }
    });
};

/*数据源*/
export const DataSource = params => {
    return request({
        url: BASE_URL + '/dataSource',
        method: 'get',
        params: params
    });
};

export const InsertDataSource = data => {
    return request({
        url: BASE_URL + '/dataSource',
        method: 'post',
        data: data
    });
};

export const DeleteDataSource = id => {
    return request({
        url: BASE_URL + '/dataSource/' + id,
        method: 'delete',
    });
};

export const UpdateDataSource = data => {
    return request({
        url: BASE_URL + '/dataSource',
        method: 'put',
        data: data
    });
};

export const AllDataSource = () => {
    return request({
        url: BASE_URL + '/dataSource/all',
        method: 'get',
    });
};

/*备份任务*/
export const BackUpList = params => {
    return request({
        url: BASE_URL + '/backUpJob',
        method: 'get',
        params: params
    });
};

export const BackUpJobRecordList = id => {
    return request({
        url: BASE_URL + '/backUpJob/' + id,
        method: 'get',
    });
};

export const InsertBackUpJob = data => {
    return request({
        url: BASE_URL + '/backUpJob',
        method: 'post',
        data: data
    });
};

export const EnableBackUpJob = id => {
    return request({
        url: BASE_URL + '/backUpJob/enable/' + id,
        method: 'get',
    });
};

export const DeleteBackUpJob = id => {
    return request({
        url: BASE_URL + '/backUpJob/' + id,
        method: 'delete',
    });
};

export const UpdateBackUpJob = data => {
    return request({
        url: BASE_URL + '/backUpJob',
        method: 'put',
        data: data
    });
};


/*公共方法*/
export const AllDataBaseByDataSource = id => {
    return request({
        url: BASE_URL + '/common/getAllDataBaseByDataSource/' + id,
        method: 'get'
    });
};

export const LastRunTime = cron => {
    return request({
        url: BASE_URL + '/common/lastRunTime?cron=' + cron,
        method: 'get'
    });
};

/*Dashboard*/
export const Dashboard = () => {
    return request({
        url: BASE_URL + '/dashboard',
        method: 'get'
    });
};


/*恢复任务*/
export const RestoreList = params => {
    return request({
        url: BASE_URL + '/restoreJob',
        method: 'get',
        params: params
    });
};

export const RestoreJobRecordList = id => {
    return request({
        url: BASE_URL + '/restoreJob/' + id,
        method: 'get',
    });
};
