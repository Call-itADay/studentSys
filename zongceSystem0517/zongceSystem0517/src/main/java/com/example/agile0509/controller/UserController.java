package com.example.agile0509.controller;


import com.example.agile0509.common.CommonResult;
import com.example.agile0509.mapper.RoleMapper;
import com.example.agile0509.mapper.UserMapper;
import com.example.agile0509.pojo.Menu;
import com.example.agile0509.pojo.Node;
import com.example.agile0509.pojo.Role;
import com.example.agile0509.service.impl.AuthServiceImpl;
import com.example.agile0509.utils.JwtTokenUtil;
import com.example.agile0509.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthServiceImpl authService;


    @GetMapping("/get/role")
    public CommonResult<?> getRole(@RequestHeader("Authorization") String authHeader) {

        // 解析Authorization请求头中的JWT令牌 Bearer access_token
        String token = authHeader.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        // 调用Authervice中的方法来获取用户ID
        int userId = authService.getUserIdByUsername(username);

        // 根据用户ID调用AuthService中的方法来获取用户角色
        List<Role> roles = authService.getRolesByUserId(userId);

        // 将Role转换为RoleVO
        List<RoleVO> roleVOList = new ArrayList<>();
        for (Role role : roles) {
            RoleVO roleVO = new RoleVO();
            roleVO.setRole(role.getName());
            roleVOList.add(roleVO);
        }

        // 封装结果并返回
        CommonResult<List<RoleVO>>result = CommonResult.success(roleVOList);
        return result;
    }

    /*
    @GetMapping("/get/auth")
    public CommonResult<?> getAuth(@RequestHeader("Authorization") String authHeader) {

        // 解析Authorization请求头中的JWT令牌 Bearer access_token
        String token = authHeader.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        // 调用Authervice中的方法来获取用户ID
        int userId = authService.getUserIdByUsername(username);

        // 根据用户ID调用AuthService中的方法来获取用户角色
        List<Role> roles = authService.getRolesByUserId(userId);

        // 构建包含角色和权限的结果对象
        List<RolePermissionVO> rolePermissions = new ArrayList<>();
        for (Role role : roles) {

            //获取角色ID
            int roleId= roleMapper.getRoleIdByName(role.getName());

            // 获取角色对应的权限列表
            List<Permission> permissions = authService.getPermissionsByRoleId(roleId);
            System.out.println(permissions);
            // 遍历每个 Permission 对象，去除 URL 中的 \r
            for (Permission permission : permissions) {
                String cleanedUrl = permission.getUrl().replaceAll("\r", "");
                permission.setUrl(cleanedUrl);
            }

            // 构建角色权限对象，并将权限列表赋值
            RolePermissionVO rolePermission = new RolePermissionVO();
            rolePermission.setRole(role.getName());
            rolePermission.setPermissions(permissions);

            // 将角色权限对象添加到结果列表
            rolePermissions.add(rolePermission);

        }
        // 封装结果并返回
        CommonResult<List<RolePermissionVO>> result = CommonResult.success(rolePermissions);
        return result;
    }
    */
    @GetMapping("/get/menu")
    public CommonResult<?> getMenu(@RequestHeader("Authorization") String authHeader) {

        // 解析Authorization请求头中的JWT令牌 Bearer access_token
        String token = authHeader.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        // 调用Authervice中的方法来获取用户ID
        int userId = authService.getUserIdByUsername(username);

        // 根据用户ID调用AuthService中的方法来获取用户角色
        List<Role> roles = authService.getRolesByUserId(userId);

        // 构建包含角色和权限的结果对象
        List<Node> nodes = new ArrayList<>();
        Set<Integer> addedNodeIds = new HashSet<>(); // 用于存储已添加的节点ID
        for (Role role : roles) {

            //获取角色ID
            int roleId = roleMapper.getRoleIdByName(role.getName());

            List<Node> roleNodes = authService.getMenuByRoleId(roleId);

            for (Node node : roleNodes) {//如果直接add很有可能导致菜单重复
                if (!addedNodeIds.contains(node.getId())) {
                    nodes.add(node);
                    addedNodeIds.add(node.getId());
                }
            }
        }
        List<Menu> menu=authService.convertToMenus(nodes);
            /*
            // 获取角色对应的权限列表
            List<Permission> permissions = authService.getMenusByRoleId(roleId,PermissionType.type1);
            // 遍历每个 Permission 对象，去除 URL 中的 \r
            for (Permission permission : permissions) {
                String cleanedUrl = permission.getUrl().replaceAll("\r", "");
                permission.setUrl(cleanedUrl);
            }

            // 构建角色权限对象，并将权限列表赋值
            RolePermissionVO rolePermission = new RolePermissionVO();
            rolePermission.setRole(role.getName());
            rolePermission.setPermissions(permissions);

            // 将角色权限对象添加到结果列表
            rolePermissions.add(rolePermission);

        }
        */
        // 封装结果并返回
        CommonResult<List<Menu>> result = CommonResult.success(menu);
        return result;
    }
}