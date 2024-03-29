package ezenweb.model.dao;

import ezenweb.model.dto.BoardDto;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BoardDao extends Dao {

// =============================== 1. 글쓰기 처리 [ 글쓰기 성공 했을 때 자동 생성된 글번호 반환, 실패 시 0 ] =============================== //
    public long doPostBoardWrite( BoardDto boardDto ){
        System.out.println("BoardDao.doPostBoardWrite");
        System.out.println("boardDto = " + boardDto);
        try {
            String sql = "insert into board(btitle, bcontent, bfile, mno, bcno) values( ?, ?, ?, ? ,? )";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,boardDto.getBtitle());
            ps.setString(2,boardDto.getBcontent());
            ps.setString(3,boardDto.getBfile());
            ps.setLong(4,boardDto.getMno());
            ps.setLong(5,boardDto.getBcno());
            int count = ps.executeUpdate();
            if(count == 1){
                rs = ps.getGeneratedKeys();
                System.out.println("rs = " + rs);
                if(rs.next()){
                    return rs.getLong(1);
                }
            }
        }catch (Exception e){
            System.out.println("boardDto = " + boardDto);
        }
        return 0;
    }

// =============================== 2. 전체 글 출력 호출 =============================== //
    public List<BoardDto> doGetBoardViewList(int startRow, int pageBoardSize, int bcno, String field, String value){
        System.out.println("BoardDao.doGetBoardViewList");
        BoardDto boardDto = null;
        List<BoardDto> list = new ArrayList<>();
        try {
            String sql = "select * from board b inner join member m on b.mno = m.no ";
                if(bcno > 0){
                    sql += " where bcno = " + bcno;
                }
                if(!value.isEmpty()) {
                    if (bcno > 0) {
                        sql += " and ";
                    } else {
                        sql += " where ";
                    }
                    sql += field + " like '%" + value + "%' ";
                }
            sql += " order by b.bdate desc limit ?, ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,startRow);
            ps.setInt(2,pageBoardSize);
            rs = ps.executeQuery();
            while(rs.next()){
                boardDto = new BoardDto(
                        rs.getLong("bno"),
                        rs.getString("btitle"),
                        rs.getString("bcontent"),
                        rs.getString("bfile"),
                        rs.getLong("bview"),
                        rs.getString("bdate"),
                        rs.getLong("mno"),
                        rs.getLong("bcno"),
                        null,
                        rs.getString("id"),
                        rs.getString("img")
                );
                list.add(boardDto);
            }
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        return list;
    }
// =============================== 2-2. 전체 게시물 수 호출 =============================== //
    public int getBoardSize(int bcno, String field, String value){
        System.out.println("BoardService.getBoardSize");
        System.out.println("value = " + value);
        try {
            String sql = "select count(*) from board b inner join member m on b.mno = m.no";
            // === 만약에 카테고리 조건이 있으면 where 추가
            if(bcno > 0){
                sql += " where b.bcno = " + bcno;
            }
            // === 만약에 검색이 있을 때
            if(!value.isEmpty()){
                if( bcno > 0){
                    sql += " and ";
                }else {
                    sql += " where ";
                }
                sql += field + " like '%" + value + "%' ";
                System.out.println(sql);
            }
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        }catch (Exception e){
            System.out.println("e = " + e);

        }
        return 0; // 없으면 0
    }
// =============================== 3-2. 개별 글 출력 시 조회수 증가 =============================== //
    public void boardViewIncrease( int bno ){
        try {
            String sql = "update board set bview = bview+1 where bno = "+bno;
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        }catch (Exception e){
            System.out.println("e = " + e);
        }
    }
// =============================== 3. 개별 글 출력 호출 =============================== //
    public BoardDto doGetBoardView( int bno ){
        System.out.println("BoardDao.doGetBoardView");
        System.out.println("bno = " + bno);
        BoardDto boardDto = null;
        try {
            String sql = "select * from board b inner join member m on b.mno = m.no where b.bno = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1,bno);
            rs = ps.executeQuery();
            if(rs.next()){
                boardDto = new BoardDto(
                        rs.getLong("bno"),
                        rs.getString("btitle"),
                        rs.getString("bcontent"),
                        rs.getString("bfile"),
                        rs.getLong("bview"),
                        rs.getString("bdate"),
                        rs.getLong("mno"),
                        rs.getLong("bcno"),
                        null,
                        rs.getString("id"),
                        rs.getString("img")
                );
            }
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        return boardDto;
    }

// =============================== 4. 개별 글 삭제 =============================== //
    public boolean doDeleteBoard(int bno){
        System.out.println("BoardDao.doDeleteBoard");
        System.out.println("bno = " + bno);
        try {
            String sql = "delete from board where bno=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,bno);
            // ps.executeUpdate()가 1이라는 것은 sql 구문을 실행을 했는데 1개 했다
            int count = ps.executeUpdate();
            if (count == 1){
                return true;
            }
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        return false;
    }

// =============================== 5. 개별 글 수정 =============================== //
    public boolean doPutBoard(BoardDto boardDto){
        System.out.println("BoardDao.doPutBoard");
        System.out.println("boardDto = " + boardDto);
        try {
            String sql = "update board set btitle = ?, bcontent = ?, bcno = ?, bfile = ? where bno = ?";
            ps = conn. prepareStatement(sql);
            ps.setString(1, boardDto.getBtitle());
            ps.setString(2, boardDto.getBcontent());
            ps.setLong(3,boardDto.getBcno());
            ps.setString(4,boardDto.getBfile());
            ps.setLong(5,boardDto.getBno());
            int count = ps.executeUpdate();
            if(count == 1){
                return true;
            }
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        return false;
    }

// =============================== 6. 게시물 작성자 인증 =============================== //
    public boolean boardWriteAuth(long bno, String mid){
        System.out.println("BoardDao.boardWriteAuth");
        System.out.println("bno = " + bno + ", mid = " + mid);
        try{
            String sql = "select * from board b inner join member m on b.mno = m.no where b.bno = ? and m.id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, bno);
            ps.setString(2,mid);
            rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        return false;
    }
// =============================== 7. 댓글 등록 =============================== //
    public boolean doPostReply( Map<String, String> map ){
        System.out.println("BoardDao.doPostReply");
        System.out.println("map = " + map);

        try {
            String sql = "insert into breply(brcontent,brindex,mno,bno) values(?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, map.get("brcontent"));
            ps.setString(2, map.get("brindex"));
            ps.setString(3, map.get("mno"));
            ps.setString(4, map.get("bno"));
            int count = ps.executeUpdate();
            if(count==1){
                return true;
            }
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        return false;
    }

// =============================== 8. 댓글 출력 =============================== //
    public List<Map<String, Object>> getReplyDo(int bno){
        System.out.println("BoardDao.getReplyDo");
        System.out.println("bno = " + bno);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String sql = "select * from breply where bno = ? and brindex = 0";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,bno);
            rs = ps.executeQuery();
            while(rs.next()){
                Map<String, Object> map = new HashMap<>();
                map.put("brno", rs.getString("brno"));
                map.put("brcontent", rs.getString("brcontent"));
                map.put("brdate", rs.getString("brdate"));
                map.put("mno", rs.getString("mno"));
                    // ============ 해당 상위 댓글의 하위 댓글들도 호출하기 ============ //
                    String subSql = "select * from breply where brindex = ? and bno = "+bno;
                    ps = conn.prepareStatement(subSql);
                    ps.setInt(1,Integer.parseInt(rs.getString("brno")));
                        // (int) 캐스팅 = 부모, 자식관계이어야 한다. vs Integer.parseInt( ) int와 String은 상하관계가 아니다
                    //  rs 사용하면 안되는 이유 : 현재 상위 댓글 출력시 rs 사용중 이므로 (while 문에서 사용중)
                ResultSet rs2 = ps.executeQuery();
                List< Map<String, Object> > subList = new ArrayList<>();
                while(rs2.next()){
                    Map<String, Object> subMap = new HashMap<>();
                    subMap.put("brno", rs2.getString("brno"));
                    subMap.put("brcontent", rs2.getString("brcontent"));
                    subMap.put("brdate", rs2.getString("brdate"));
                    subMap.put("mno", rs2.getString("mno"));
                    subList.add(subMap);
                }
                map.put("subReply", subList);
                list.add(map);
            }
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        return list;
    }
}
