function dateValidation( element, year, month, day ) {
	var y = document.getElementsByName( year )[0].value;
	var m = document.getElementsByName( month )[0].value;
	var d = document.getElementsByName( day )[0].value;
	if ( y && m && d ) {
		var ds = new Date( y, m-1, d );
		if ( ds.getFullYear() != y || ds.getMonth() != m-1 || ds.getDate() != d ) {
			alert("日付がおかしいよ！！");
			return element.value = '';
		}
	}
}

function addMember( dataIx ) {

//    var dataIx = targetEle.id;
	var baseTbl = document.getElementById('tablebodybase');
	var baseTr = baseTbl.getElementsByTagName('tr')[dataIx];
	var baseTeam = baseTr.cells[0];
	var baseName = baseTr.cells[1];

	var target = document.getElementById('tablebody');

    for( var i = 0; i < target.rows.length; i++ )
    {
        var cells = target.rows[ i ].cells;

        if( cells[0].innerHTML == baseTeam.innerHTML &&
        	cells[1].innerHTML == baseName.innerHTML )
        {
//			alert("該当メンバは追加済みです");
			return;
        }
    }

	var newTr = target.insertRow( -1 );

	var teamTd = newTr.insertCell( -1 );
	teamTd.appendChild(document.createTextNode(baseTeam.innerHTML));
	teamTd.className='team';

	var nameTd = newTr.insertCell( -1 );
	nameTd.appendChild(document.createTextNode(baseName.innerHTML));
	nameTd.className='name';

	var delJsTd = newTr.insertCell( -1 );
	delJsTd.className='btn';

	var delJs = document.createElement('input');
    delJs.type = 'button';
    delJs.value = "削除";
    delJs.id = target.rows.length -1;
    delJs.addEventListener('click', function(){
    	delMember(this.id);
    });
    delJs.style.cssText = "width: 100%";
    delJsTd.appendChild(delJs);

    var delJs = document.createElement('input');
    delJs.type = 'hidden';
    delJs.id  ='memberlist';
    delJs.name  = 'memberlist';
    delJs.value= baseName.innerHTML;
    delJsTd.appendChild(delJs);

}

function delMember( dataIx ) {

//	var dataIx = targetEle.id;

	var baseTbl = document.getElementById('tablebody');
	baseTbl.deleteRow(dataIx);

	for( var i = 0; i < baseTbl.rows.length; i++ )
    {
		var cells = baseTbl.rows[ i ].cells;
		cells[2].getElementsByTagName("input")[0].id = i;
    }

}

function refine()
{
//	var teamlist = document.getElementsById("teamlist");

	var teamselect = false;
	var ret = document.getElementsByName("teamlist")
	if(team.value != "")
	{
		teamselect = true;
	}

	var nameselect = false;
	var re = document.getElementsByName("search")[0];
	if(re.value != "")
	{
		nameselect = true;
	}

	if(teamselect || nameselect)
	{

		var target = document.getElementById('tablebodybase');
		for( var i = 0; i < target.rows.length; i++ )
		{
			var cells = target.rows[ i ].cells;

		    var hit = false;
		    if(teamselect)
		    {
			    for (j = 0; j < teamlist.length; j++)
			    {
			    	if(team == teamlist[j].value)
			    	{
			    		if(teamlist[j].selected == false)
			    		{
			    			target.rows[ i ].style.display = "none";
				    		hit = true;
			    		}
			    		break;
			    	}
			    }
		    }
		    if(hit)
		    {
		    	continue;
		    }

		    if(nameselect)
		    {
			    var name = cells[1].innerHTML;
			    if( name.match(re.value) != null){
			    	target.rows[ i ].style.display = "";
			    }else{
			    	target.rows[ i ].style.display = "none";
				}
		    }
		    else
		    {
		    	target.rows[ i ].style.display = "";
		    }
		}
	}
	else
	{
		allDisplay();
	}
}

function refine2()
{
	var re = document.getElementsByName("search")[0];
	re.value = "";

	refine();
}

function allDisplay()
{
	var target = document.getElementById('tablebodybase');
	for( var i = 0; i < target.rows.length; i++ )
	{
		target.rows[ i ].style.display = "";
	}
}

function preset()
{
	document.getElementById("teamList").selectedIndex = 0;
}



window.onload = function(){
	// ページ読み込み時に実行したい処理
	// ボタンの活性/非活性を切り替え
	// メンバリストの絞り込み
	refine();

	var id = document.getElementsByName("id")[0];
	if(id.value != "")
	{
		var target = document.getElementsByName('setevent')[0];
		target.value = "更新";
	}
}

