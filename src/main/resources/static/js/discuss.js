function like(btn, entityType, entityId, entityUserId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId},
        function (data) {
            data = $.parseJSON(data);
            // 说明请求成功
            if (data.code == 0) {
                // 修改点赞数量
                $(btn).children("i").text(data.likeCount);
                // 修改点赞状态
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : '赞');
            } else { // 请求失败
                alert(data.msg);
            }
        }
    );
}