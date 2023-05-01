function selectElement(selectedElement) {
    console.log('window open');
    console.log('selectElement = ' + selectedElement);
    addDataToForm(selectedElement);
    window.close();
}

// 선택된 데이터를 add form 에 추가하는 함수
function addDataToForm(ele) {
    console.log('addDateToForm' + ele);

    let elements = ele.getElementsByTagName("td");
    console.log("선택한 이미지 태그: ", elements[0].querySelector('img').getAttribute('src'));
    console.log("선택한 td 태그: ", elements[1].innerText);
    console.log("선택한 td 태그: ", elements[2].innerText);

    let data = {
        imageURL: elements[0].querySelector('img').getAttribute('src'),
        title: elements[1].innerText,
        creator: elements[2].innerText
    };

    console.log("선택한 이미지 태그: ", data.imageURL);
    console.log("선택한 td 태그: ", data.title);
    console.log("선택한 td 태그: ", data.creator);

    // 부모 창으로 element 전달
    opener.document.getElementById("add_form_imageURL").value = data.imageURL;
    opener.document.getElementById("add_form_title").value = data.title;
    opener.document.getElementById("add_form_creator").value = data.creator;

}

