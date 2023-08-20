package cn.xiaolin.gateway.grpc;

import cn.xiaolin.api.grpc.UserPermProto;
import cn.xiaolin.api.grpc.UserPermServiceGrpc;
import com.google.protobuf.Int64Value;
import com.google.protobuf.ProtocolStringList;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 用户权限gRPC客户端
 * @create 2023/8/13
 */

@Service
@RequiredArgsConstructor
public class UserPermClient {

    private final ManagedChannel managedChannel;

    public List<String> getPermissions(Long userId) {
        UserPermServiceGrpc.UserPermServiceBlockingStub stub = UserPermServiceGrpc.newBlockingStub(managedChannel);
        UserPermProto.PermsResponse response = stub.getPermByUserId(Int64Value.of(userId));
        ProtocolStringList nameList = response.getNameList();
        return nameList.stream().toList();
    }

    public List<String> getRoles(Long userId) {
        UserPermServiceGrpc.UserPermServiceBlockingStub stub = UserPermServiceGrpc.newBlockingStub(managedChannel);
        UserPermProto.RolesResponse response = stub.getRoleByUserId(Int64Value.of(userId));
        ProtocolStringList nameList = response.getNameList();
        return nameList.stream().toList();
    }
}
