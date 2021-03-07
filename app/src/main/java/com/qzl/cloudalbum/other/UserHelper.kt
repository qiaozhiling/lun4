package com.qzl.cloudalbum.other

object UserHelper {
    private var cookie: String = ""
    private var email: String = ""
    private var password: String? = null
    private var showHidden: Boolean = false


    fun setCookie(cookie: String?) {
        cookie?.let {
            this.cookie = cookie
        }
    }


    fun getCookie() = cookie

    fun setEmail(email: String) {
        this.email = email
    }

    fun getEmail() = email

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getPassword() = password

    fun getShowHidden() = showHidden

    fun setShowHidden(s: Boolean) {
        showHidden = s
    }

    //文件名合法
    fun nameInLaw(name: String): Boolean = when {
        (name == "") -> false
        (name.contains(" ")) -> false
        (name.matches(Regex("^[^/\\\\?*<>:,]+\$"))) -> true
        else ->false
    }


    /*





    //Throw 返回是否成功
    //重命名
    suspend fun reName(oldPath: String, newName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).rename(oldPath, newName)
                        .await()
                !myResult.err
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //改变文件（夹）隐藏状态
    suspend fun changeStatus(targetFilePath: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).changeHideStatus(targetFilePath)
                        .await()
                !myResult.err

            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //删除文件（夹
    suspend fun deleteFile(pathToDelete: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).delete(pathToDelete).await()
                Log.i("deeeeeeeeelte", myResult.toString())
                !myResult.err

            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //上传图片
    suspend fun uploadPic(parentPath: String, file: MultipartBody.Part): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).upload(parentPath, file)
                        .await()
                !myResult.err
            } catch (e: Exception) {
                throw e
            }
        }
    }











    //Throw 返回是否成功
    //验证邮箱
    suspend fun verifyEmail(code: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    if (code == null) ServiceCreator.create(CldAbService::class.java).verify()
                        .await()
                    else ServiceCreator.create(CldAbService::class.java).verify(code).await()

                val err = result.err
                val data = result.data
                !err && data
            } catch (e: Exception) {
                throw e
            }
        }
    }
*/


}