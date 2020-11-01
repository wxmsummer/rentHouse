contract RentHouse {

    // 房东结构体
    struct Landlord {
        // id 身份唯一标识
        uint64 id
        string name
        string phone
        uint64 account_id
    }

    // 租客结构体
    struct Renter {
        // id 身份唯一标识
        uint64 id
        string name
        string phone
        uint64 account_id
    }

    // 账户结构体
    struct Account{
        // id，唯一标识身份，考虑使用公钥？？
        uint64 id
        // 余额
        float balance
    }

    // 租房信息结构体 
    struct RentalInfo{
        int64 id            // 租房信息id
        string owner_id     // 房东id
        string room_name    // 房名
        string address      // 地址
        string model        // 户型，如1室1厅1卫
        float area          // 面积/m^2
        int rent_term       // 租期/月
        float price         // 租金/元每月
        float cash_pledge   // 押金/元
        string [9]picture   // 房间照片，数组存储，最多9张
        string note         // 备注，其他信息，如wifi是否通畅、水电费等
    }

    struct House{
        int64 id        // 房间id
        string secret   // 房间密码
    }

    // 根据账户id 初始化账户
    public string init_account(uint64 id) {

        db_set(id, 0)

        return 'success'

    }

    // 往账户充值
    public string deposit(uint64 id, float amount) {

        balance = db_get(id)
        db_set(id, balance + amount)

        return 'success'

    }

    // 更新账户余额
    public string update_account(Account account) {

        db_set(account.id, account.balance)

        return 'success'
    }

    // 根据用户id获取账户
    public Account get_account_by_id(uint64 id){
        return Account{
            id: id
            balance: db_get(id)
        }
    }

    // 往智能合约里抵押资产 
    // id标识抵押者，amount为抵押数量
    public string pledge(uint64 id, float amount){
        
        // 获取账户
        account = get_account_by_id(id) 
        // 获取智能合约账户
        contract_account = get_account_by_id(contract_id)
        // 检查余额
        if account.balance > amount{
            // 扣除账户余额
            account.balance -= amount
            
            // 添加智能合约余额
            contract_account.balance += amount
        }

        update_account(account)
        update_account(contract_account)
        
    }

    // 房东发布租房信息
    // id标识房东，info为具体信息
    // 返回租房信息id
    public int64 add_rental_info(uint64 id, RentalInfo info){
        
        // 序列化info
        str_info =  serialize(info)

        db_set(id, str_info)

        return rent_id
    }

    // 根据租房id获取租房信息
    public RentalInfo get_rental_info(int64 id){
        
        db_get(id)
    }

    // 根据租客id获取房间
    public House get_house_by_renter_id(uint64 id) {

    }

    // 签署租房合约，成功后返回密码锁给租客
    public string sign_contract() {

        return secret
    }

    // 缴纳租金
    // id标识租客，amount为缴纳金额
    public string pay_rent(string renter_id, string landlord_id, float amount) {
        
        renter_account = get_account_by_id(renter_id) 
        landlord_account = get_account_by_id(landlord_id)

        if renter_account.balance > amount{
            renter_account.balance -= amount
            landlord_account.balance += amount
        }

        update_account(renter_account)
        update_account(landlord_account)

        return "success"

    }

    // 租客违约，则应该扣除租客一定押金后退还剩余押金，并变更密码锁？
    public string renter_break_contract(string renter_id, string landlord_id) {
        
        // 哪位租客的哪套房违约
        house = get_house_by_renter_id(renter_id)
        // 获取押金
        cash_pledge = house.cash_pledge

        renter_account = get_account_by_id(renter_id)

        landlord_account = get_account_by_id(landlord_id) 

        // 获取智能合约账户
        contract_account = get_account_by_id(contract_id)

        // 违约金 = 押金 * 违约比例
        liquidated_damage = cash_pledge * break_percent
        // 退还金额
        back_amount = cash_pledge - liquidated_damage

        if contract_account.balance > cash_pledge {
            contract_account.balance -= cash_pledge
        }

        landlord_account.balance += liquidated_damage

        renter_account.balance += back_amount

        update_account(landlord_account)
        update_account(renter_account)
        update_account(contract_account)

        // 更换密码锁
        change_secret(house.id)

    }

    // 房东违约，则应该扣除房东部分抵押金，同时返还租客押金
    public string landlord_break_contract(string renter_id, string landlord_id) {
        
    }

}