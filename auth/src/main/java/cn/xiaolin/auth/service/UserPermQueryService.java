package cn.xiaolin.auth.service;

import java.util.List;

public interface UserPermQueryService {

    List<String> listPermissionNames(Long userId);

    List<String> listRoleNames(Long userId);
}
