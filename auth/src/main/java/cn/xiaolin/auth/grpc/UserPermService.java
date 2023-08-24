package cn.xiaolin.auth.grpc;

import cn.xiaolin.api.grpc.UserPermProto;
import cn.xiaolin.api.grpc.UserPermServiceGrpc;
import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.service.SysPermissionService;
import cn.xiaolin.auth.service.SysRoleService;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.util.List;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 用户权限gRPC服务
 * @create 2023/8/13
 */
@GRpcService
@RequiredArgsConstructor
public class UserPermService extends UserPermServiceGrpc.UserPermServiceImplBase {
    private final SysPermissionService sysPermissionService;
    private final SysRoleService sysRoleService;

    /**
     * @param request 用户id
     * @param responseObserver 权限列表
     */
    @Override
    public void getPermByUserId(Int64Value request, StreamObserver<UserPermProto.PermsResponse> responseObserver) {
        long userId = request.getValue();
        List<SysPermission> permissions = sysPermissionService.listPermsByUserId(userId);
        List<String> permNames = permissions.stream()
                .map(SysPermission::getName)
                .toList();
        UserPermProto.PermsResponse response = UserPermProto.PermsResponse.newBuilder().addAllName(permNames).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * @param request 用户id
     * @param responseObserver 角色列表
     */
    @Override
    public void getRoleByUserId(Int64Value request, StreamObserver<UserPermProto.RolesResponse> responseObserver) {
        long userId = request.getValue();
        List<SysRole> roles = sysRoleService.listRolesByUserId(userId);
        List<String> roleNames = roles.stream()
                .map(SysRole::getName)
                .toList();
        UserPermProto.RolesResponse response = UserPermProto.RolesResponse.newBuilder().addAllName(roleNames).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
