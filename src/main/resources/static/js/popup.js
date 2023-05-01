function openPopup() {
//카테고리 버튼에 따라 도서 검색 or 영화 검색 선택
    var selectBox = document.getElementById("category");
    var selectedValue = selectBox.options[selectBox.selectedIndex].value;
    var path = ""
    window.console.log("openPopUp 선택한 요소: ", selectedValue);

    if (selectedValue === "BOOK") {
        path = "/board/book-search";
    } else if (selectedValue === "MOVIE") {
        path = "/board/movie-search";
    }
    var popup = window.open(path, "도서/영화 검색", "width=500, height=500");
    popup.focus();
}



