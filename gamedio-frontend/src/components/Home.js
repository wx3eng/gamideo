import React from 'react';
import { Button, List, message, Tabs, Tooltip } from 'antd';
import { StarOutlined, StarFilled } from '@ant-design/icons';
import { addFavoriteItem, deleteFavoriteItem } from '../utils';

const { TabPane } = Tabs;
const tabKeys = {
  Streams: 'stream',
  Videos: 'videos',
  Clips: 'clips',
}

const processUrl = (url) => url
  .replace('%{height}', '300')
  .replace('%{width}', '450')
  .replace('{height}', '300')
  .replace('{width}', '450');

const renderFavIcon = (item, loggedIn, favs, favOnChange) => {
  const isFav = favs.find((fav) => fav.id === item.id);

  const favOnClick = () => {
    if (isFav) {
      deleteFavoriteItem(item).then(() => {
        favOnChange();
      }).catch(err => {
        message.error(err.message)
      })

      return;
    }

    addFavoriteItem(item).then(() => {
      favOnChange();
    }).catch(err => {
      message.error(err.message)
    })
  }

  return (
    <>
      {
        loggedIn &&
        <Tooltip title={isFav ? "Remove from My Favorites" : "Add to My Favorites"} color="rgb(162, 0, 255)">
          <Button shape="circle" icon={isFav ? <StarFilled /> : <StarOutlined />} onClick={favOnClick} />
        </Tooltip>
      }
    </>
  )
}

const renderList = (data, loggedIn, favs, favOnChange) => {
  console.log(data)
  if (data.length === 0) {
    return (
      <>
        <b>Instructions to use this website:</b>
        <div>Click on the left navigation panel to select game to watch</div>
        <div>Select Streams/Videos/Clips to view from the ribbon</div>
        <div>Once registered and logged in, you will be able to like/unlike resources. We will recommend resources based on your game preference</div>
      </>
    )
  }
  return (
    <List
      itemLayout="vertical"
      size="large"
      dataSource={data}
      pagination={{
        onChange: page => {
          console.log(page);
        },
        pageSize: 5,
      }}
      renderItem={item => (
        <List.Item
          style={{ marginRight: '20px' }}
          actions={[renderFavIcon(item, loggedIn, favs, favOnChange)]}
          extra={
            <a href={item.url} target="_blank" rel="noopener noreferrer">
              <img
                width={450}
                height={300}
                alt="Placeholder"
                src={processUrl(item.thumbnail_url)}
              />
            </a>
          }
        >
          <List.Item.Meta
            title={<span style={{ overflow: 'hidden', textOverflow: 'ellipsis', width: 250, paddingLeft: '1em' }}>
              <Tooltip title={"Broadcast by: " + item.broadcaster_name} placement="topLeft" color="rgb(162, 0, 255)">
                <span>{item.title}</span>
              </Tooltip>
            </span>}
            description={
              <>
                <div>Click the link below to watch</div>
                <a href={item.url} target="_blank" rel="noopener noreferrer">
                  {item.url}
                </a>
              </>
            }
          />
        </List.Item>
      )}
    />
  )
}

const Home = ({ resources, loggedIn, favoriteItems, favoriteOnChange }) => {
  const { VIDEO, STREAM, CLIP } = resources;
  const { VIDEO: favVideos, STREAM: favStreams, CLIP: favClips } = favoriteItems;

  return (
    <Tabs
      defaultActiveKey={tabKeys.Streams}
    >
      <TabPane tab="Streams" key={tabKeys.Streams} style={{ height: '640px', overflow: 'auto' }} forceRender={true}>
        {renderList(STREAM, loggedIn, favStreams, favoriteOnChange)}
      </TabPane>
      <TabPane tab="Videos" key={tabKeys.Videos} style={{ height: '640px', overflow: 'auto' }} forceRender={true}>
        {renderList(VIDEO, loggedIn, favVideos, favoriteOnChange)}
      </TabPane>
      <TabPane tab="Clips" key={tabKeys.Clips} style={{ height: '640px', overflow: 'auto' }} forceRender={true}>
        {renderList(CLIP, loggedIn, favClips, favoriteOnChange)}
      </TabPane>
    </Tabs>
  );
}

export default Home;